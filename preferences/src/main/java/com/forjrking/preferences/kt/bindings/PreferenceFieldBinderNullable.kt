package com.forjrking.preferences.kt.bindings

import android.util.Log
import com.forjrking.preferences.crypt.Crypt
import com.forjrking.preferences.kt.PreferenceHolder
import java.lang.reflect.Type
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal open class PreferenceFieldBinderNullable<T : Any>(
    private val clazz: KClass<T>,
    private val type: Type,
    private val key: String?,
    private val caching: Boolean,
    private val crypt: Crypt?
) : ReadWriteProperty<PreferenceHolder, T?>, Clearable {

    override fun clear(thisRef: PreferenceHolder, property: KProperty<*>) {
        setValue(thisRef, property, null)
    }

    override fun clearCache() {
        propertySet = false
        field = null
    }

    @Volatile
    private var propertySet: Boolean = false

    @Volatile
    private var field: T? = null

    override operator fun getValue(thisRef: PreferenceHolder, property: KProperty<*>): T? {
        val key = getKey(key, property)
        if (!thisRef.preferences.contains(key)) return null
        //缓存开启时候不用读取？
        if (caching && propertySet) return field
        //除了需要序列化的对象等  其他读取sp也是内存 感觉意义不大  需要测试性能
        val newValue = thisRef.preferences.getValue(clazz, type, key, crypt)
        field = newValue
        propertySet = true
        return newValue
    }

    override fun setValue(thisRef: PreferenceHolder, property: KProperty<*>, value: T?) {
        if (caching) {
            if (value == field && isOutsideOfCache(value)) {
                Log.d("PreferenceHolder", "value is the same as the cache")
                return
            }
            field = value
            propertySet = true
        }
        if (value == null) {
            thisRef.edit.remove(getKey(key, property)).apply()
        } else {
            thisRef.edit.apply { putValue(clazz, getKey(key, property), value, crypt) }.apply()
        }
    }
}