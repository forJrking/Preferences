package com.forjrking.preferences.kt.bindings

import android.content.SharedPreferences
import com.forjrking.preferences.crypt.Crypt
import com.forjrking.preferences.kt.PreferenceHolder.Companion.serializer
import com.forjrking.preferences.serialize.Serializer
import java.lang.reflect.Type
import kotlin.reflect.KClass

internal fun SharedPreferences.Editor.putValue(
    clazz: KClass<*>,
    value: Any,
    key: String,
    crypt: Crypt?
) {
    when (clazz.simpleName) {
        "Int" -> putInt(key, value as Int)
        "Float" -> putFloat(key, value as Float)
        "Long" -> putLong(key, value as Long)
        "Boolean" -> putBoolean(key, value as Boolean)
        "String" -> {
            val message = if (crypt != null) crypt.encrypt(value as String?) else value as String?
            putString(key, message)
        }
        else -> {
            val message = if (crypt != null) crypt.encrypt(value.serialize()) else value.serialize()
            putString(key, message)
        }
    }
}

internal fun <T : Any> SharedPreferences.getFromPreference(
    clazz: KClass<T>, type: Type, default: T?, key: String, crypt: Crypt?
): T? =
    when (clazz.simpleName) {
        "Int" -> getInt(key, default as Int) as? T
        "Float" -> getFloat(key, default as Float) as? T
        "Long" -> getLong(key, default as Long) as? T
        "Boolean" -> getBoolean(key, default as Boolean) as? T
        "String" -> {
            val text = getString(key, default as? String)
            val result = if (crypt != null) crypt.decrypt(text) ?: default as? String else text
            result as? T
        }
        else -> {
            val value = getString(key, null)
            val result = if (crypt != null) crypt.decrypt(value) else value
            result?.deserialize(type) ?: default
        }
    }

internal fun <T : Any> SharedPreferences.getFromPreference(
    clazz: KClass<T>, type: Type, key: String, crypt: Crypt?
): T? = getFromPreference(clazz, type, getDefault(clazz), key, crypt)

private fun <T : Any> getDefault(clazz: KClass<T>): T? = when (clazz.simpleName) {
    "Long" -> 0L
    "Int" -> 0
    "Boolean" -> false
    "Float" -> 0.0F
    else -> null
} as? T

/*********序列化*********/
private fun <T : Any> String.deserialize(type: Type): T? =
    getSerializer().deserialize(this, type) as? T

private fun <T> T.serialize() = getSerializer().serialize(this)

fun getSerializer(): Serializer {
    if (serializer == null) {
        throw ExceptionInInitializerError("serializer is null")
    } else {
        return serializer!!
    }
}
/*********序列化*********/
