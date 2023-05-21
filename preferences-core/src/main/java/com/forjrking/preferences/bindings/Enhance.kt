package com.forjrking.preferences.bindings

import com.forjrking.preferences.PreferencesOwner
import kotlin.reflect.KProperty

interface Enhance {
    fun key(property: KProperty<*>): String
    fun clear(thisRef: PreferencesOwner, property: KProperty<*>)
    fun clearCache()
}
