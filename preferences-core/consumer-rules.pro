-keep class com.forjrking.preferences.provide.** {*;}
-keep class com.forjrking.preferences.serialize.TypeToken {*;}
-keep class * extends com.forjrking.preferences.serialize.TypeToken {*;}
# Keep all native methods, their classes and any classes in their descriptors
-keepclasseswithmembers,includedescriptorclasses class com.tencent.mmkv.** {
    native <methods>;
    long nativeHandle;
    private static *** onMMKVCRCCheckFail(***);
    private static *** onMMKVFileLengthError(***);
    private static *** mmkvLogImp(...);
    private static *** onContentChangedByOuterProcess(***);
}
