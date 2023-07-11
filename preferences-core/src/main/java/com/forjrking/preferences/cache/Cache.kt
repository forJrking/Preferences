package com.forjrking.preferences.cache

internal interface Cache<T> {
    fun acquire(action: () -> T): T
    fun incept(value: T, action: () -> Unit)
    fun reset()
}
