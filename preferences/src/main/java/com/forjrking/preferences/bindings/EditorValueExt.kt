package com.forjrking.preferences.bindings

import android.content.SharedPreferences
import com.forjrking.preferences.PreferencesOwner.Companion.serializer
import com.forjrking.preferences.crypt.Crypt
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal fun SharedPreferences.Editor.putValue(
    clazz: KClass<*>, key: String, value: Any?, crypt: Crypt?
) = when (clazz) {
    Int::class -> putInt(key, value as Int)
    Float::class -> putFloat(key, value as Float)
    Long::class -> putLong(key, value as Long)
    Boolean::class -> putBoolean(key, value as Boolean)
    String::class -> putString(key, (value as String?).encrypt(crypt))
    else -> {
        val message = serializer.serialize(value)
        putString(key, message.encrypt(crypt))
    }
}

internal fun SharedPreferences.getValue(
    clazz: KClass<*>, type: Type, key: String, default: Any?, crypt: Crypt?
): Any? = when (clazz) {
    Int::class -> getInt(key, default as Int)
    Float::class -> getFloat(key, default as Float)
    Long::class -> getLong(key, default as Long)
    Boolean::class -> getBoolean(key, default as Boolean)
    String::class -> getString(key, default as? String).decrypt(crypt) ?: default
    else -> {
        val result = getString(key, null).decrypt(crypt)
        serializer.deserialize(result, type) ?: default
    }
}

/*********加解密*********/
private fun String?.encrypt(crypt: Crypt?): String? = crypt?.encrypt(this) ?: this
private fun String?.decrypt(crypt: Crypt?): String? = crypt?.decrypt(this) ?: this

/*********加解密*********/
internal fun getKey(keySet: String?, property: KProperty<*>) = keySet ?: "${property.name}Key"

/*****获取默认值***/
internal fun getDefault(clazz: KClass<*>): Any? = when (clazz) {
    Int::class -> 0
    Long::class -> 0L
    Float::class -> 0.0F
    Boolean::class -> false
    else -> null
}
