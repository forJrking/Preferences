package com.forjrking.preferences.kt.bindings

import kotlin.reflect.KProperty

internal fun getKey(keySet: String?, property: KProperty<*>) = keySet ?: "${property.name}Key"

internal fun <T : Any> isOutsideOfCache(value: T?) =
    value !is Collection<*> && value !is Map<*, *> && value is Array<*>
