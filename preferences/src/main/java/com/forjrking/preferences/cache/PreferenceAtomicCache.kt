package com.forjrking.preferences.cache

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * @description: Preference 缓存类,但是不能缓存多进程的数据, 可能取不到其他进程的update
 * @author: forjrking
 * @date: 2023/5/6 10:36 PM
 */
internal class PreferenceAtomicCache<T>(private val caching: Boolean) {

    private val filled = AtomicBoolean(false)

    private val atomicCache = AtomicReference<T>()

    fun acquire(action: () -> T): T {
        if (filled.get()) return atomicCache.get()
        val value = action.invoke()
        if (caching) {
            atomicCache.set(value)
            filled.set(true)
        }
        return value
    }

    fun incept(value: T, action: () -> Unit) {
        if (caching) {
            if (value == atomicCache.get() && isBasicType(value)) return
            atomicCache.set(value)
            filled.set(true)
        }
        action.invoke()
    }

    fun reset() {
        filled.set(false)
        atomicCache.set(null)
    }

    /**
     * sp editor 支持的类型, 不需要走序列化.
     */
    private fun <T> isBasicType(value: T?) = when (value) {
        is Int, is Float, is Long, is Boolean, is String -> true
        else -> false
    }
}