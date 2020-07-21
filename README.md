## PowerfulPreference

### 简介

重新封装[PreferenceHolder](https://github.com/MarcinMoskala/PreferenceHolder)，让SharedPreferences使用更加简单

> 优化SharedPreferences缺陷降低ANR，利用ContentProviderr让原生sp支持多进程，支持`MMKV`，支持数据加密

### 集成

1    添加依赖和初始化

```kotlin

repositories {
    ...
    maven { url 'https://jitpack.io' }
}

dependencies {
	implementation 'com.google.code.gson:gson:2.8.5' //存储对象需要  非必须
	implementation 'com.tencent:mmkv-static:1.2.1'	// mmkv  非必须
	implementation 'com.github.forjrking:Preferences:1.2.1'	// mmkv  非必须
}

 //Application 中初始化MMKV
  MMKV.initialize(this) ...
 // 必须
  PreferenceHolder.context = this.applicationContext
 // 非必须  用于序列化对象数据，sp不建议存大量数据
  PreferenceHolder.serializer = GsonSerializer()
```

2   写一个类添加方法

```kotlin
/*
 * @param name xml名称 默认为包名
 * @param cryptKey 加密密钥  
 * 注意｛原生sp多进程不支持加密  多进程本身数据不安全而且性能比较差综合考虑不加密｝
 * @param isMMKV  是否使用mmkv
 * @param isMultiProcess 是否使用多进程  建议mmkv搭配使用 sp性能很差
 */
object TestSP : PreferenceHolder("name","cyptyKey",isMMKV,isMultiProcess) {
    var testStr: String by bindToPreferenceField("")
    var coin: Long by bindToPreferenceField(0L)
    var tes: String? by bindToPreferenceFieldNullable()
    //需要使用 GsonSerializer
    var savedGame: Game? by bindToPreferenceFieldNullable()
}
//getValue
val str = TestSP.testStr
val coin = TestSP.coin
println(str) //"" or "something"
//setValue
TestSP.testStr = "AAAX${Random().nextInt(20)}"
TestSP.coin = 100
```



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

