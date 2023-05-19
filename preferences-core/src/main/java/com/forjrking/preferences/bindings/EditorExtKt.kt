@file:Suppress("UNCHECKED_CAST")

package com.forjrking.preferences.bindings

import android.content.SharedPreferences
import com.forjrking.preferences.PreferencesOwner.Companion.serializer
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal fun SharedPreferences.Editor.putValue(
    clazz: KClass<*>, key: String, value: Any?
) = when (clazz) {
    Int::class -> putInt(key, value as Int)
    Float::class -> putFloat(key, value as Float)
    Long::class -> putLong(key, value as Long)
    Boolean::class -> putBoolean(key, value as Boolean)
    String::class -> putString(key, value as String?)
    Set::class -> when {
        (value as Set<String>?).isStringSet -> putStringSet(key, value)
        else -> putString(key, serializer.serialize(value))
    }

    else -> putString(key, serializer.serialize(value))
}

internal fun SharedPreferences.getValue(
    clazz: KClass<*>, type: Type, key: String, default: Any?
): Any? = when (clazz) {
    Int::class -> getInt(key, default as Int)
    Float::class -> getFloat(key, default as Float)
    Long::class -> getLong(key, default as Long)
    Boolean::class -> getBoolean(key, default as Boolean)
    String::class -> getString(key, default as? String)
    Set::class -> when {
        (default as Set<String>?).isStringSet -> getStringSet(key, default)
        else -> serializer.deserialize(getString(key, null), type) ?: default
    }

    else -> serializer.deserialize(getString(key, null), type) ?: default
}

private val Set<String>?.isStringSet: Boolean
    get() = if (this == null) false else (filterIsInstance<String>().size == size)

internal fun getKey(keySet: String?, property: KProperty<*>) = keySet ?: "${property.name}Key"

/*****获取默认值***/
internal fun getDefault(clazz: KClass<*>): Any? = when (clazz) {
    Int::class -> 0
    Long::class -> 0L
    Float::class -> 0.0F
    Boolean::class -> false
    Set::class -> emptySet<String>()
    else -> null
}