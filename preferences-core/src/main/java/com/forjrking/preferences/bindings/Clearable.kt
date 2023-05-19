package com.forjrking.preferences.bindings

import com.forjrking.preferences.PreferencesOwner
import kotlin.reflect.KProperty

interface Clearable {
    fun clear(thisRef: PreferencesOwner, property: KProperty<*>)
    fun clearCache()
}