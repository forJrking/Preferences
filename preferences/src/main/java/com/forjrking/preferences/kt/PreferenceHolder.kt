package com.forjrking.preferences.kt

import android.content.Context
import android.content.SharedPreferences
import com.forjrking.preferences.crypt.AesCrypt
import com.forjrking.preferences.crypt.Crypt
import com.forjrking.preferences.kt.bindings.*
import com.forjrking.preferences.serialize.Serializer
import com.forjrking.preferences.provide.createSharedPreferences
import java.lang.reflect.Type
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

/***
 *SharedPreferences使用可以配合mmkv
 * @param name xml名称 this::class.java.simpleName
 * @param cryptKey 加密密钥  ｛原生sp多进程不支持加密  多进程本身数据不安全而且性能比较差综合考虑不加密｝
 * @param isMMKV  是否使用mmkv
 * @param isMultiProcess 是否使用多进程  建议mmkv搭配使用 sp性能很差
 */
open class PreferenceHolder(
    name: String? = null,
    cryptKey: String? = null,
    isMMKV: Boolean = false,
    isMultiProcess: Boolean = false
) {

    open val preferences: SharedPreferences by lazy {
        context!!.createSharedPreferences(name, cryptKey, isMultiProcess, isMMKV)
    }

    private var crypt: Crypt? = null

    init {
        if (!isMMKV && !cryptKey.isNullOrEmpty()) {
            crypt = AesCrypt(cryptKey)
        }
    }

    protected inline fun <reified T : Any> bindToPreferenceField(
        default: T,
        key: String? = null,
        caching: Boolean = true
    ): ReadWriteProperty<PreferenceHolder, T> =
        bindToPreferenceField(T::class, object : TypeToken<T>() {}.type, default, key, caching)

    protected inline fun <reified T : Any> bindToPreferenceFieldNullable(
        key: String? = null,
        caching: Boolean = true
    ): ReadWriteProperty<PreferenceHolder, T?> =
        bindToPreferenceFieldNullable(T::class, object : TypeToken<T>() {}.type, key, caching)

    protected fun <T : Any> bindToPreferenceField(
        clazz: KClass<T>,
        type: Type,
        default: T,
        key: String?,
        caching: Boolean = true
    ): ReadWriteProperty<PreferenceHolder, T> =
        if (caching) PreferenceFieldBinderCaching(clazz, type, default, key, crypt)
        else PreferenceFieldBinder(clazz, type, default, key, crypt)

    protected fun <T : Any> bindToPreferenceFieldNullable(
        clazz: KClass<T>,
        type: Type,
        key: String?,
        caching: Boolean = true
    ): ReadWriteProperty<PreferenceHolder, T?> =
        if (caching) PreferenceFieldBinderNullableCaching(clazz, type, key, crypt)
        else PreferenceFieldBinderNullable(clazz, type, key, crypt)

    /**
     *  Function used to clear all SharedPreference and PreferenceHolder data. Useful especially
     *  during tests or when implementing Logout functionality.
     */
    fun clear(safety: Boolean = true) {
        forEachDelegate { delegate, property ->
            if (safety && property.name.startsWith("_")) return@forEachDelegate
            delegate.clear(this, property)
        }
    }

    fun clearCache() {
        forEachDelegate { delegate, _ ->
            delegate.clearCache()
        }
    }

    private fun forEachDelegate(f: (Clearable, KProperty<*>) -> Unit) {
        val properties = this::class.declaredMemberProperties
            .filterIsInstance<KProperty1<SharedPreferences, *>>()
        for (p in properties) {
            val prevAccessible = p.isAccessible
            if (!prevAccessible) p.isAccessible = true
            val delegate = p.getDelegate(preferences)
            if (delegate is Clearable) f(delegate, p)
            p.isAccessible = prevAccessible
        }
    }

    companion object {
        var context: Context? = null
            get() {
                if (field == null) {
                    throw IllegalStateException("PreferenceHolder is not initialed")
                }
                return field
            }
            set(value) {
                field = value?.applicationContext
            }

        var serializer: Serializer? = null
    }
}