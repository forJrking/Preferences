package com.forjrking.preferences.kt.bindings

import android.util.Log
import com.forjrking.preferences.crypt.Crypt
import com.forjrking.preferences.kt.PreferenceHolder
import java.lang.reflect.Type
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal class PreferenceFieldBinder<T : Any>(
    private val clazz: KClass<T>,
    private val type: Type,
    private val default: T,
    private val key: String?,
    private val caching: Boolean,
    private val crypt: Crypt?
) : ReadWriteProperty<PreferenceHolder, T>, Clearable {

    private var field: T? = null

    override fun clear(thisRef: PreferenceHolder, property: KProperty<*>) {
        setValue(thisRef, property, default)
    }

    override fun clearCache() {
        field = null
    }

    override operator fun getValue(thisRef: PreferenceHolder, property: KProperty<*>): T =
        thisRef.preferences.getValue(clazz, type, getKey(key, property), crypt, default) as T

    override fun setValue(thisRef: PreferenceHolder, property: KProperty<*>, value: T) {
        if (caching) {
            if (value == field && isOutsideOfCache(value)) {
                Log.d("PreferenceHolder", "value is the same as the cache")
                return
            }
            field = value
        }
        thisRef.edit.apply {
            putValue(clazz, getKey(key, property), value, crypt)
        }.apply()
    }
}