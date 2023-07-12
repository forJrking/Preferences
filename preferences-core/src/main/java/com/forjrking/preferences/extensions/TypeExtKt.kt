package com.forjrking.preferences.extensions

import kotlin.reflect.KClass
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

internal val TYPE_SET_STRING by lazy { typeOf<Set<String>>().javaType.toString() }

/*****获取默认值***/
internal val KClass<*>.defaultValue
    get() = when (this) {
        Int::class -> 0
        Long::class -> 0L
        Float::class -> 0.0F
        Boolean::class -> false
        else -> null
    }