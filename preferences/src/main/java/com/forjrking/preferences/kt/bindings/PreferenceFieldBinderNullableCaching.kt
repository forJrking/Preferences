package com.forjrking.preferences.kt.bindings

import android.content.SharedPreferences
import com.forjrking.preferences.crypt.Crypt
import com.forjrking.preferences.kt.PreferenceHolder
import java.lang.reflect.Type
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal class PreferenceFieldBinderNullableCaching<T : Any>(
    private val clazz: KClass<T>,
    private val type: Type,
    private val key: String?,
    private val crypt: Crypt?
) : ReadWriteProperty<PreferenceHolder, T?>, Clearable {

    override fun clear(thisRef: PreferenceHolder, property: KProperty<*>) {
        setValue(thisRef, property, null)
    }

    override fun clearCache() {
        propertySet = false
        field = null
    }

    var propertySet: Boolean = false
    var field: T? = null

    override operator fun getValue(thisRef: PreferenceHolder, property: KProperty<*>): T? =
        readAndSetValue(thisRef.preferences, property)

    private fun readAndSetValue(pref: SharedPreferences, property: KProperty<*>): T? {
        val newValue = readValue(pref, property)
        field = newValue
        propertySet = true
        return newValue
    }

    override fun setValue(thisRef: PreferenceHolder, property: KProperty<*>, value: T?) {
        propertySet = true
        if (value == field) return
        field = value
        saveNewValue(thisRef.preferences, property, value)
    }

    private fun saveNewValue(pref: SharedPreferences, property: KProperty<*>, value: T?) {
        if (value == null) {
            pref.edit().remove(getKey(key, property)).apply()
        } else {
            pref.edit().apply { putValue(clazz, value, getKey(key, property), crypt) }.apply()
        }
    }

    private fun readValue(pref: SharedPreferences, property: KProperty<*>): T? {
        val key = getKey(key, property)
        if (!pref.contains(key)) return null
        return pref.getFromPreference(clazz, type, key, crypt)
    }
}