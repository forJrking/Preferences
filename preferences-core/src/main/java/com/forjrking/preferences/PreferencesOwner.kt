package com.forjrking.preferences

import android.app.Application
import android.content.SharedPreferences
import com.forjrking.preferences.bindings.Clearable
import com.forjrking.preferences.bindings.PreferenceFieldBinder
import com.forjrking.preferences.provide.createSharedPreferences
import com.forjrking.preferences.serialize.Serializer
import com.forjrking.preferences.serialize.TypeToken
import kotlin.properties.Delegates.notNull
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

/***
 *SharedPreferences使用可以配合mmkv
 * @param name xml名称 this::class.java.simpleName 如果使用包名不同,类字段相同会覆盖值
 * @param cryptKey 加密密钥  ｛原生sp多进程不支持加密 ｝
 * @param isMMKV  是否使用mmkv
 * @param isMultiProcess 是否使用多进程. 建议mmkv搭配使用, MultiProcessSp性能很差, 另外多进程默认关闭缓存
 * 1. 当出现获取值始终不是期望的时候,请优先考虑关闭缓存.
 */
open class PreferencesOwner(
    private val name: String? = null,
    private val cryptKey: String? = null,
    protected val isMMKV: Boolean = false,
    protected val isMultiProcess: Boolean = false
) {

    val preferences: SharedPreferences by lazy {
        if (!isInitialized()) {
            throw IllegalStateException("PreferenceHolder is not initialed")
        }
        context.createSharedPreferences(
            name ?: this::class.qualifiedName,
            cryptKey,
            isMultiProcess,
            isMMKV
        )
    }

    /** DES: 减小edit实例化时候集合多次创建开销 */
    internal val edit: SharedPreferences.Editor by lazy { preferences.edit() }

    /**
     * @param default 默认值
     * @param key 自定义key
     * @param caching 缓存开关
     * */
    protected inline fun <reified T> preferenceBinding(
        default: T, key: String? = null, caching: Boolean = !isMultiProcess
    ): ReadWriteProperty<PreferencesOwner, T> = PreferenceFieldBinder(
        clazz = T::class,
        type = object : TypeToken<T>() {}.type,
        default = default,
        key = key,
        caching = caching
    )

    protected inline fun <reified T> preferenceNullableBinding(
        key: String? = null, caching: Boolean = !isMultiProcess
    ): ReadWriteProperty<PreferencesOwner, T?> = PreferenceFieldBinder(
        clazz = T::class,
        type = object : TypeToken<T>() {}.type,
        default = null,
        key = key,
        caching = caching
    )

    /**
     *  Function used to clear all SharedPreference and PreferenceHolder data. Useful especially
     *  during tests or when implementing Logout functionality.
     */
    fun clear(safety: Boolean = true) = forEachDelegate { clear, property ->
        if (safety && property.name.startsWith("_")) return@forEachDelegate
        clear.clear(this, property)
    }

    /** DES: 清理缓存字段 */
    fun clearCache() = forEachDelegate { clear, _ ->
        clear.clearCache()
    }

    /**
     * 获取所有key-value 默认根据配置是否加解密决定 mmkv默认必须解密 此功能无效
     * */
    fun getAll(): MutableMap<String, *>? {
        val receiver = this
        return if (receiver.isMMKV) {
            HashMap<String, Any?>().also {
                val properties = receiver::class.declaredMemberProperties
                    .filterIsInstance<KProperty1<PreferencesOwner, *>>()
                for (p in properties) {
                    val prevAccessible = p.isAccessible
                    if (!prevAccessible) p.isAccessible = true
                    p.getDelegate(receiver)?.let { _ ->
                        it[p.name] = p.get(receiver)
                    }
                    p.isAccessible = prevAccessible
                }
            }
        } else {
            preferences.all
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
        /** DES: 为了防止内存泄漏 */
        lateinit var context: Application

        /** DES: isInitialized 放到伴生对象外面会报错。。。 */
        fun isInitialized(): Boolean = ::context.isInitialized

        /** DES: 序列化接口 */
        var serializer: Serializer by notNull()
    }
}
