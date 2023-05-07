## PowerfulPreference [![](https://jitpack.io/v/forJrking/Preferences.svg)](https://jitpack.io/#forJrking/Preferences)

### 简介

重新封装[PreferenceHolder](https://github.com/MarcinMoskala/PreferenceHolder)，让SharedPreferences使用更加简单

> `SharedPreferences` 对于 Android 开发者来说是最轻量级的KeyValue持久化组件了，相信大家都有自己的一套封装。而且微信基于mmap实现的MMKV高性能kv组件大家也都不陌生了。那么kotlin到来之后，大家还在沿用java的用法么？今天给大家带来你没有见过的船新版本，贪玩。。哦不`SharedPreferences`的写法，无意间我发现一个开源库 PreferenceHolder ，简洁的写法一下子吸引了我，只需要变量的赋值和读取就可以实现sp繁琐的方法，让我想上手来试试，结果发现很多小问题，然后进行了一波打磨优化。最终实现了`PowefulPreferenceHolder`支持多进程、数据加解密、MMKV，另外对原生`SharedPreferences`进行 Hook优化降低ANR，各种极致优化。

支持sp和mmkv的getAll方法

### 项目github地址

https://github.com/forJrking/Preferences   请start表示对我的支持哦！

### PowefulPreferenceHolder

1. 添加依赖

```kotlin
repositories {
    ...
    maven { url 'https://jitpack.io' }
}

dependencies {
   implementation 'com.github.forJrking:Preferences:1.+' // 必须
   implementation 'com.google.code.gson:gson:2.8.5' //存储对象需要  (非必须)
   implementation 'com.tencent:mmkv-static:1.2.1' // mmkv         (非必须)
}

  // 必须Application 中初始化
  PreferenceHolder.context = this.application
  // 非必须 如果使用MMKV请初始化并且配置相关
  MMKV.initialize(this) ... 
  // 非必须 用于序列化对象数据，sp不建议存大量数据 最好换用数据库
  PreferenceHolder.serializer = GsonSerializer()
```

2. 写一个object类，必须使用kt

```kotlin
object TestSP : PreferenceHolder() {
    var value: Long by bindToPreferenceField(0L)
}

//读取sp
val value = TestSP.value
println(value) // 0 or 100
//存入sp
TestSP.value = 100
//getAll
TestSP.getAll()?.forEach {
    Log.d("TAG", "name:${it.key} value:${it.value}")
}
```

3. 其他api演示

```kotlin
/*
 * @param name xml名称           默认为实现类类名，为了防止不同类使用相同字段覆盖数据问题
 * @param cryptKey  		加密密钥 null 表示不用加密
 * @param isMMKV    		是否使用mmkv 默认false
 * @param isMultiProcess 	是否使用多进程 建议mmkv sp性能很差 默认false
 */
object TestSP : PreferenceHolder("name","cyptyKey",isMMKV,isMultiProcess) {
    //带默认值 == getString(key,default) 不可赋值 null
    var testStr: String by bindToPreferenceField('default') 
   
    var coin: Long by bindToPreferenceField(0L)
    
    var tes: String? by bindToPreferenceFieldNullable() //默认值为 null 可以为其赋值 null
    
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
