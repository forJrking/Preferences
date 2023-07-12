package com.forjrking.preferences.bindings

import com.forjrking.preferences.PreferencesOwner
import kotlin.reflect.KProperty

/**
 * @Des: 功能扩展接口 帮助sp 清理缓存
 * @Author: forjrking
 * @Time: 2023/5/22 18:32
 * @Version: 1.0.0
 **/
internal interface Clearable {
    fun key(property: KProperty<*>): String
    fun clear(thisRef: PreferencesOwner, property: KProperty<*>)
    fun clearCache()
}
