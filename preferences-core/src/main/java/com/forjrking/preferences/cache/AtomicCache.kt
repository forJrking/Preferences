package com.forjrking.preferences.cache

import androidx.annotation.VisibleForTesting
import java.util.concurrent.atomic.AtomicReference

/**
 * @description: Preference 缓存类
 * @author: forjrking
 * @date: 2023/5/20 10:36 PM
 */
@PublishedApi
internal class AtomicCache<T>(private val caching: Boolean) : Cache<T> {

    private val atomicCache = AtomicReference<T>()

    private val cacheSnapshot = AtomicReference<String>(null)

    override fun acquire(action: () -> T): T {
        if (caching && cacheSnapshot.get() != null) return atomicCache.get()
        val value = action.invoke()
        if (caching) {
            atomicCache.set(value)
            cacheSnapshot.set(value.toString())
        }
        return value
    }

    override fun incept(value: T, action: () -> Unit) {
        if (caching) {
            if (sameCaching(value, atomicCache.get())) return
            atomicCache.set(value)
            cacheSnapshot.set(value.toString())
        }
        action.invoke()
    }

    override fun reset() {
        cacheSnapshot.set(null)
        atomicCache.set(null)
    }

    /**
     * 集合, data class 修改数据后 数据一样的情况不需要重新插入
     */
    @VisibleForTesting
    fun sameCaching(newValue: T, cacheValue: T) = newValue == cacheValue && newValue.toString() == cacheSnapshot.get()
}
