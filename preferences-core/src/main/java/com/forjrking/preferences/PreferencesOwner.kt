package com.forjrking.preferences

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.forjrking.preferences.bindings.Enhance
import com.forjrking.preferences.bindings.PreferenceFieldBinder
import com.forjrking.preferences.cache.AtomicCache
import com.forjrking.preferences.provide.createSharedPreferences
import com.forjrking.preferences.serialize.Serializer
import com.forjrking.preferences.serialize.TypeToken.Companion.typeOf
import kotlin.properties.Delegates.notNull
import kotlin.properties.Delegates.vetoable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * SharedPreferences使用可以配合mmkv 当出现获取值始终不是期望的时候,请优先考虑关闭缓存.
 * @param name xml名称 this::class.java.simpleName 如果使用包名不同,类字段相同会覆盖值
 * @param cryptKey 加密密钥
 * @param isMMKV  是否使用mmkv
 * @param isMultiProcess 是否使用多进程. 建议mmkv搭配使用, MultiProcessSp性能很差, 另外多进程默认关闭缓存
 */
open class PreferencesOwner(
    private val name: String? = null,
    private val cryptKey: String? = null,
    protected val isMMKV: Boolean = false,
    protected val isMultiProcess: Boolean = false
) {

    val preferences: SharedPreferences by lazy {
        context?.createSharedPreferences(
            name = name ?: this::class.simpleName?.lowercase(),
            cryptKey = cryptKey,
            isMMKV = isMMKV,
            isMultiProcess = isMultiProcess
        ) ?: throw IllegalStateException("context is not initialized or applicationContext")
    }

    /** DES: 减小edit实例化时候集合多次创建开销 */
    internal val edit: SharedPreferences.Editor by lazy { preferences.edit() }

    /**
     * @param default 默认值
     * @param key 自定义key
     * @param caching 缓存开关
     * sp需要序列化时候传递 type
     *  val KClass<*>.isBasic
     *      get() = when (this) {
     *          Int::class, Float::class, Long::class, Boolean::class, String::class -> true
     *          else -> false
     *      }
     * */
    protected inline fun <reified T> preferenceBinding(
        default: T, key: String? = null, caching: Boolean = !isMultiProcess
    ): ReadWriteProperty<PreferencesOwner, T> = PreferenceFieldBinder(
        clazz = T::class,
        type = typeOf<T>().javaType,
        default = default,
        key = key,
        cache = AtomicCache(caching)
    )

    protected inline fun <reified T> preferenceNullableBinding(
        key: String? = null, caching: Boolean = !isMultiProcess
    ): ReadWriteProperty<PreferencesOwner, T?> = PreferenceFieldBinder(
        clazz = T::class,
        type = typeOf<T>().javaType,
        default = null,
        key = key,
        cache = AtomicCache(caching)
    )

    /**
     *  当你有重要数据要保留的时候请开头命名为: _'key'
     *  Function used to clear all SharedPreference and PreferencesOwner data. Useful especially
     *  during tests or when implementing Logout functionality.
     */
    fun clear(safety: Boolean = true) = forEachDelegate { enhance, property ->
        if (safety && enhance.key(property).startsWith("_")) return@forEachDelegate
        enhance.clear(this, property)
    }

    /** DES: 清理缓存字段 */
    fun clearCache() = forEachDelegate { enhance, _ ->
        enhance.clearCache()
    }

    /**
     * 获取所有key-value
     * @unRaw 熟肉 true  表示获取到真实key,包括自定义key
     *        生肉 false 表示获取到key数据为字段命名
     * */
    fun getAll(unRaw: Boolean = true): MutableMap<String, *>? {
        return if (isMMKV) {
            HashMap<String, Any?>().also {
                forEachDelegate { enhance, property ->
                    val key = if (unRaw) enhance.key(property) else property.name
                    it[key] = property.get(this)
                }
            }
        } else {
            preferences.all
        }
    }

    private fun forEachDelegate(onEach: (Enhance, KProperty1<PreferencesOwner, *>) -> Unit) {
        val properties = this::class.declaredMemberProperties.filterIsInstance<KProperty1<PreferencesOwner, *>>()
        for (p in properties) {
            val prevAccessible = p.isAccessible
            if (!prevAccessible) p.isAccessible = true
            val delegate = p.getDelegate(this)
            if (delegate is Enhance) onEach(delegate, p)
            p.isAccessible = prevAccessible
        }
    }

    companion object {
        /** DES: 为了防止内存泄漏 请务必使用Application*/
        var context: Context? by vetoable(null) { _, _, newValue ->
            newValue is Application
        }

        /** DES: 序列化接口 */
        var serializer: Serializer by notNull()
    }
}
