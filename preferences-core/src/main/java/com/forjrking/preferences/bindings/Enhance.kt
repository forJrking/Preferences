package com.forjrking.preferences.bindings

import com.forjrking.preferences.PreferencesOwner
import com.forjrking.preferences.serialize.TypeToken
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * @Des: 功能扩展接口 帮助sp 清理缓存
 * @Author: forjrking
 * @Time: 2023/5/22 18:32
 * @Version: 1.0.0
 **/
interface Enhance {
    fun key(property: KProperty<*>): String
    fun clear(thisRef: PreferencesOwner, property: KProperty<*>)
    fun clearCache()
}

/*****获取默认值***/
internal val KClass<*>.defaultValue
    get() = when (this) {
        Int::class -> 0
        Long::class -> 0L
        Float::class -> 0.0F
        Boolean::class -> false
        else -> null
    }

/**
 * sp支持的基本数据类型 不需要序列化判断
 */
val KClass<*>.isBasic
    get() = when (this) {
        Int::class, Float::class, Long::class, Boolean::class, String::class -> true
        else -> false
    }

/**
 * sp需要序列化时候传递 type
 */
inline fun <reified T> type() = if (T::class.isBasic) TypeToken.UNIT_TYPE else object : TypeToken<T>() {}.type
