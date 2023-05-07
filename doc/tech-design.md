
## **解读PreferenceHolder**

### PreferenceHolder让SharedPreferences变得简洁

```kotlin
//初始化
PreferenceHolder.serializer = GsonSerializer(Gson())
PreferenceHolder.setContext(applicationContext)
//定义操作类
object Pref: PreferenceHolder() {
    var isFirstInstall: Boolean by bindToPreferenceField(false)
    var time: Long? by bindToPreferenceFieldNullable()
}
//取值
if(Pref.isFirstInstall) //...
//存值
Pref.isFirstInstall = true
//清理所有数据
Pref.clear()
//支持复杂对象、集合等 使用gson序列化成字符串
var savedGame: Game? by bindToPreferenceFieldNullable()
```

怎么样？完全不用再写 `getString(..)``putString(..)`,再去定义一堆眼花缭乱的`KEY`,内部默认使用 `字段名称+“Key”`为键，这样可以从代码定义上避免重复KEY，另外还支持自定义key，如下

```kotlin
//单日操作的时间记录
var tempStr: String by bindToPreferenceField("0","temp_${day()}")
//获取今天时间拼接key
fun day(times: Long = System.currentTimeMillis()): String {
    val df = SimpleDateFormat("yyyyMMdd", Locale.US)
    return df.format(times)
}
```

### PreferenceHolder 的优缺点

优点：

- 避免定义大量字符串key和出现重复key
- 简洁的委托模式不用再书写`get(..) set(..)`
- 支持序列化、反序列化对象数据
- 支持 registerOnSharedPreferenceChangeListener(..)

缺点:

- 不支持 `getAll() remove(..)`
- 不支持自定义`xml`名称
- 不支持加解密
- 不支持 MMKV
- 不支持多进程
- 不能获取 commit 返回值
- 对集合序列化存储有bug

看起来已经非常强大好用了，实际PreferenceHolder还有个bug，在文章最后说明和修复。作为一个追求完美的程序员，我有时候会在项目中使用MMKV来提升性能，或者跨进程数据通信需求，偶尔还要加密保证用户数据安全，作为极客这些我全都要。接下来就需要剖析原理进行打磨了。

### PreferenceHolder 优化打磨

1. 源码分析解读

```kotlin
by bindToPreferenceField(...) 方法实际调用为
class PreferenceFieldBinder<T : Any>(...) : ReadWriteProperty<*, T>{ ... }
```

看到`ReadWriteProperty `似乎很熟悉啊 ，Kotlin 的 [属性监听委托](https://www.cnblogs.com/nicolas2019/p/11003895.html) 内部就用的`ObservableProperty `，而它就是 `ReadWriteProperty `的实现

```kotlin
 /**
  * 定义一个属性委托于Delegates.vetoable方法返回的ReadWriteProperty对象
  * Delegates.vetoable满足条件才能修改成功
  */
 var listenerProperty: Int by Delegates.vetoable(0, { property, oldValue, newValue ->
     println("监听到属性变化：property->${property.name} oldValue->$oldValue newValue->$newValue")
     newValue > 0//满足条件修改成功
 })
```

真相大白了，原来是通过属性监听委托，然后在`ReadWriteProperty`接口的实现方法 `getValue(..)` `setValue(..)`实现了SharedPreferences 的 `get` 和 `set` ,再次感叹 Kotlin 真香啊！！

2. 加入MMKV 优化 SharedPreferences性能

MMKV本身实现了SharedPreferences，这就简单啦，首先定义一个扩展函数，添加支持多进程的`SharedPreferences`和MMKV，`MultiProcessSharedPreferences`是MMKV性能对比源码中使用 `ContentProvider`实现的支持多进程的 `SharedPreferences`，有同学可能会说`SharedPreferences`支持多进程啊，但是在高版本中已经标记废弃，实际使用中也会有取不到数据问题。`SharedPreferencesHelper` 是开发高手课程中张邵文老师提供的，优化了由于`QueuedWork`缺陷导致`SharedPreferences`出现的ANR。其他关于`SharedPreferences`的槽点文章后面有个链接，大家可以去学习下，内容非常全面。

   ```kotlin
   /* 生成支持多进程的mmkv 和sp
    * @param name xml名称  默认包名，建议给名字否则出现操作同key问题
    * @param cryptKey 加密密钥 mmkv加密密钥 SharedPreferences 内部方法不支持加密
    * @param isMMKV  是否使用mmkv
    * @param isMultiProcess 是否使用多进程  建议mmkv搭配使用
    * 此方法不提供MMKV初始化需要自己操作配置
    */
   @JvmOverloads
   fun Context.createSharedPreferences(
       name: String? = null,cryptKey: String? = null,
       isMultiProcess: Boolean = false,isMMKV: Boolean = false
   ): SharedPreferences {
       val xmlName = "${if (name.isNullOrEmpty()) packageName else name}_kv"
       return if (isMMKV) {
           if (com.tencent.mmkv.MMKV.getRootDir().isNullOrEmpty()) {
               Log.e("MMKV", "You forgot to initialize MMKV")
               com.tencent.mmkv.MMKV.initialize(this)
           }
           // 这样使用MMKV没引入使用不会classNotFound
           val mode = if (isMultiProcess) com.tencent.mmkv.MMKV.MULTI_PROCESS_MODE
           else com.tencent.mmkv.MMKV.SINGLE_PROCESS_MODE
           com.tencent.mmkv.MMKV.mmkvWithID(xmlName, mode, cryptKey)
       } else {
           val mode = Context.MODE_PRIVATE
           if (isMultiProcess) {
               MultiProcessSharedPreferences.getSharedPreferences(this, xmlName, mode)
           } else {
               SharedPreferencesHelper.getSharedPreferences(this, xmlName, mode)
           }
       }
   }
   ```

3. 添加AES加解密

由于MMKV本身支持加密，所以在上面初始化MMKV时候开启即可。但是`SharedPreferences`没有提供加解密接口，所以我们在后面数据`get()、set()`时候添加加解密方法即可，仅仅对String和序列化数据加密。来看下最终实现，详细代码请看 `crypt`包中实现

```kotlin
/*********加解密扩展方法*********/
private fun String?.encrypt(crypt: Crypt?): String? = crypt?.encrypt(this) ?: this
private fun String?.decrypt(crypt: Crypt?): String? = crypt?.decrypt(this) ?: this
/*********加解密*********/
... 加密
String::class -> {
    val message = value as String?
    putString(key, message.encrypt(crypt))
}
... 解密
String::class -> {
    val text = getString(key, default as? String)
    val result = text.decrypt(crypt) ?: default
    result as? T
}
```

### PreferenceHolder  Bug

我需要完成一个类似7天签到功能，临时用`PreferenceHolder`存储数据，发现签到后数据却没有变化。

```kotlin
data class Daily(var dayIndex: String, var isClmiaed: Boolean)
//Sp操作类
object ObjectTest : PreferenceHolder() {
	var taskDaily: MutableList<Daily>? by bindToPreferenceFieldNullable()
}
//获取数据
val items = ObjectTest.taskDaily
//修正数据
...
ObjectTest.taskDaily = items
items?.forEach {
    it.isClmiaed = true
}
//存储数据
ObjectTest.taskDaily = items

//之后获取数据并不是自己最后存储的
val items = ObjectTest.taskDaily 数据中所有的 (isClmiaed == false)

//原因默认开启缓存模式不兼容集合
PreferenceHolder by bindToPreferenceFieldxxx  最后缓存参数默认true
override fun setValue(thisRef: PreferenceHolder, property: KProperty<*>, value: T) {
	//获取的数据修改后集合的对象并没有变化
    if (value == field) return //缓存数据和外面进来的数据‘相同’不进行存储
	field = value
}

//1 针对集合等关闭缓存 参数caching = false
val xxx = by bindToPreferenceField(default,null,false)
//2 修改源码类型判断集合数组智能关闭
if (caching) {
    if (value == field && !(value is Collection<*> || value is Map<*, *> || value is Array<*>)) {
        Log.d("PreferenceHolder", "value is the same as the cache")
        return
    }
    field = value
}
```

### 参考资料

[细数 SharedPreferences 的那些槽点 ](https://www.cnblogs.com/bingxinshuo/p/11427208.html)

[kotlin的委托](https://www.cnblogs.com/nicolas2019/p/11003895.html)


### Retrofit 思想封装的实现

性能较差不建议使用 仅做学习

```kotlin
@SpConfig
interface JavaSP {
    String getCoin();
    void setCoin(String coin);
    boolean isFirstShare(boolean isFirst);
    void setFirstShare(boolean isFirst);
    void CLEAR();
}

 val javaSP = SpRetrofit.create(this, JavaSP::class.java)
 javaSP.coin = "212"  //赋值
 val coin = javaSP.coin //取值
 val firstShare = javaSP.isFirstShare(true) //取值 默认值
 javaSP.setFirstShare(false)
```
