## Preference-ktx [![](https://jitpack.io/v/forJrking/Preferences.svg)](https://jitpack.io/#forJrking/Preferences)

### 简介

> `Preferences` 基于Kotlin委托实现了`SharedPreferences`支持多进程、数据加解密、MMKV，另外对原生`SharedPreferences`进行Hook优化降低ANR，缓存优化。

### 特点
- 支持多进程
- 支持MMKV无缝接入
- 支持sp和mmkv的getAll方法
- 支持数据加解密(基于androidx.security)

### 项目github地址

https://github.com/forJrking/Preferences   请start表示对我的支持哦！

### Preferences

1. 添加依赖

```kotlin
repositories {
    ...
    maven { url 'https://jitpack.io' }
}

dependencies {
   implementation 'com.github.forjrking.pref:pref-core:2.0.0' // 必须
   implementation 'com.github.forjrking.pref:pref-gson:2.0.0' // 非必须
   implementation 'com.github.forjrking.pref:pref-ktx:2.0.0' // 非必须
   implementation 'com.google.code.gson:gson:2.8.5' //存储对象需要(非必须)
   implementation 'com.tencent:mmkv-static:1.2.1' // mmkv支持加密和多进程(非必须)
   implementation 'androidx.security:security-crypto:1.0.0' // sp加密支持(非必须)
}

  // 必须Application 中初始化, 依赖了pref-gson会自动初始化
  PreferenceHolder.context = this.context
  // 非必须 如果使用MMKV请初始化并且配置相关
  MMKV.initialize(this) ... 
  // 非必须 用于序列化对象和集合等数据，sp不建议存大量数据, 依赖了pref-gson会自动初始化
  PreferenceHolder.serializer = GsonSerializer()
```

2. 写一个object类，必须使用kt

```kotlin
object TestSP : PreferencesOwner() {
    var value: Long by preferenceBinding(0L)
    var objCase: Game? by preferenceNullableBinding()
}

// 存入sp
TestSP.value = 100
// 读取sp
val value = TestSP.value
println(value) // 0 or 100
// getAll
TestSP.getAll()?.forEach {
    Log.d("TAG", "name:${it.key} value:${it.value}")
}
```

3. 其他api演示

```kotlin
/*
 * @param name xml名称       默认为实现类类名，为了防止不同类使用相同字段覆盖数据问题
 * @param cryptKey  		加密密钥 null 表示不用加密 需要api >= 23
 * @param isMMKV    		是否使用mmkv 默认false
 * @param isMultiProcess 	是否使用多进程 建议mmkv sp性能很差 默认false
 */
object TestSP : PreferencesOwner("name","cyptKey",isMMKV,isMultiProcess) {
    //带默认值 == getString(key,default) 不可赋值 null
    var testStr: String by preferenceBinding('default')
   
    var coin: Long by preferenceBinding(0L)
    
    var tes: String? by preferenceNullableBinding() //默认值为 null 可以为其赋值 null
    
    //支持所有sp支持的数据类型 以及 object 需要初始化上一步的 GsonSerializer
    var savedGame: Game? by bindToPreferenceFieldNullable()
}
//getValue 读取sp
val str = TestSP.testStr
val coin = TestSP.coin
println(str) //"" or "something"

//setValue 存入sp
TestSP.testStr = "AX${Random().nextInt(20)}"
TestSP.coin = 100
```

4. Java 支持问题

这个比较简单由于委托方法仅仅支持kt，所以先用kt书写操作类，然后在java中使用`getXXX()、setXXX()`来使用，一样非常牛X
