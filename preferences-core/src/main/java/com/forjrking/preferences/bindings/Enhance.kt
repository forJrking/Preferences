package com.forjrking.preferences.bindings

import com.forjrking.preferences.PreferencesOwner
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * @Des: 功能扩展接口 帮助sp 清理缓存
 * @Author: forjrking
 * @Time: 2023/5/22 18:32
 * @Version: 1.0.0
 **/
internal interface Enhance {
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
