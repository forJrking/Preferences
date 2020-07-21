package com.forjrking.preferences.kt.bindings

import com.forjrking.preferences.kt.PreferenceHolder
import kotlin.reflect.KProperty

interface Clearable {
    fun clear(thisRef: PreferenceHolder, property: KProperty<*>)
    fun clearCache()
}