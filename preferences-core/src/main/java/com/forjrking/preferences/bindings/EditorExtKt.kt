@file:Suppress("UNCHECKED_CAST")

package com.forjrking.preferences.bindings

import android.content.SharedPreferences
import com.forjrking.preferences.PreferencesOwner.Companion.serializer
import com.forjrking.preferences.serialize.TypeToken.Companion.typeOf
import java.lang.reflect.Type
import kotlin.reflect.KClass

private val TYPE_SET_STRING by lazy { typeOf<Set<String>>().javaType.toString() }

internal fun SharedPreferences.Editor.putValue(
    clazz: KClass<*>, type: Type, key: String, value: Any?
) = when (clazz) {
    Int::class -> putInt(key, value as Int)
    Float::class -> putFloat(key, value as Float)
    Long::class -> putLong(key, value as Long)
    Boolean::class -> putBoolean(key, value as Boolean)
    String::class -> putString(key, value as String?)
    Set::class -> when (type.toString()) {
        TYPE_SET_STRING -> putStringSet(key, value as Set<String>?)
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
    Set::class -> when (type.toString()) {
        TYPE_SET_STRING -> getStringSet(key, default as Set<String>?)
        else -> serializer.deserialize(getString(key, null), type) ?: default
    }

    else -> serializer.deserialize(getString(key, null), type) ?: default
}
