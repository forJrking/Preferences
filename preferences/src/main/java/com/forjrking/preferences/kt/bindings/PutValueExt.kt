package com.forjrking.preferences.kt.bindings

import android.content.SharedPreferences
import com.forjrking.preferences.crypt.Crypt
import com.forjrking.preferences.kt.PreferenceHolder.Companion.serializer
import java.lang.reflect.Type
import kotlin.reflect.KClass

internal fun SharedPreferences.Editor.putValue(
    clazz: KClass<*>, key: String, value: Any, crypt: Crypt?
) {
    when (clazz) {
        Int::class -> putInt(key, value as Int)
        Float::class -> putFloat(key, value as Float)
        Long::class -> putLong(key, value as Long)
        Boolean::class -> putBoolean(key, value as Boolean)
        String::class -> {
            val message = value as String?
            putString(key, message.encrypt(crypt))
        }
        else -> {
            val message = value.serialize()
            putString(key, message.encrypt(crypt))
        }
    }
}

internal fun <T : Any> SharedPreferences.getValue(
    clazz: KClass<T>, type: Type, key: String, crypt: Crypt?, default: T? = getDefault(clazz)
): T? =
    when (clazz) {
        Int::class -> getInt(key, default as Int) as? T
        Float::class -> getFloat(key, default as Float) as? T
        Long::class -> getLong(key, default as Long) as? T
        Boolean::class -> getBoolean(key, default as Boolean) as? T
        String::class -> {
            val text = getString(key, default as? String)
            val result = text.decrypt(crypt) ?: default
            result as? T
        }
        else -> {
            val result = getString(key, null).decrypt(crypt)
            result?.deserialize(type) ?: default
        }
    }

/*获取默认值*/
private fun <T : Any> getDefault(clazz: KClass<T>): T? = when (clazz) {
    Long::class -> 0L
    Int::class -> 0
    Boolean::class -> false
    Float::class -> 0.0F
    else -> null
} as? T

/*********序列化*********/
private fun <T : Any> String.deserialize(type: Type): T? = serializer?.deserialize(this, type) as? T
private fun <T> T.serialize() = serializer?.serialize(this)
/*********序列化*********/
/*********加解密*********/
private fun String?.encrypt(crypt: Crypt?): String? = crypt?.encrypt(this) ?: this
private fun String?.decrypt(crypt: Crypt?): String? = crypt?.decrypt(this) ?: this
/*********加解密*********/