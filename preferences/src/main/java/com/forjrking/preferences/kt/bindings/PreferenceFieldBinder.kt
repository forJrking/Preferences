package com.forjrking.preferences.kt.bindings

import android.content.SharedPreferences
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
    private val crypt: Crypt?
) : ReadWriteProperty<PreferenceHolder, T>, Clearable {

    override fun clear(thisRef: PreferenceHolder, property: KProperty<*>) {
        setValue(thisRef, property, default)
    }

    override fun clearCache() {
    }

    override operator fun getValue(thisRef: PreferenceHolder, property: KProperty<*>): T = thisRef.preferences.getValue(property)

    override fun setValue(thisRef: PreferenceHolder, property: KProperty<*>, value: T) {
        thisRef.preferences.edit().apply { putValue(clazz, value, getKey(key, property),crypt) }.apply()
    }

    private fun SharedPreferences.getValue(property: KProperty<*>): T {
        val key = getKey(key, property)
        return getFromPreference(clazz, type, default, key,crypt) as T
    }
}