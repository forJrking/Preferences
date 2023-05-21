package com.forjrking.preferences.bindings

import com.forjrking.preferences.PreferencesOwner
import com.forjrking.preferences.cache.PreferenceAtomicCache
import java.lang.reflect.Type
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class PreferenceFieldBinder<T>(
    private val clazz: KClass<*>,
    private val type: Type,
    private val default: T,
    private val key: String?,
    private val caching: Boolean
) : ReadWriteProperty<PreferencesOwner, T>, Clearable {

    private val atomicCache by lazy { PreferenceAtomicCache<T>(caching) }

    override fun clear(thisRef: PreferencesOwner, property: KProperty<*>) {
        setValue(thisRef, property, default)
    }

    override fun clearCache() {
        atomicCache.reset()
    }

    override operator fun getValue(thisRef: PreferencesOwner, property: KProperty<*>): T {
        val key = getKey(key, property)
        if (!thisRef.preferences.contains(key)) return default
        //缓存开启时候不用读取？除了需要序列化的对象等  其他读取sp也是内存 感觉意义不大  需要测试性能
        return atomicCache.acquire {
            if (default == null) {
                thisRef.preferences.getValue(clazz, type, key, getDefault(clazz))
            } else {
                thisRef.preferences.getValue(clazz, type, key, default)
            } as T
        }
    }

    override fun setValue(thisRef: PreferencesOwner, property: KProperty<*>, value: T) {
        atomicCache.incept(value) {
            if (value == default) {
                thisRef.edit.remove(getKey(key, property))
            } else {
                thisRef.edit.apply {
                    putValue(clazz, type, getKey(key, property), value as? Any)
                }
            }.apply()
        }
    }
}
