package com.forjrking.preferences.provide

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * @DES: sp的hook
 * @CHANGED: 岛主
 * @TIME: 2019/1/15 0015 上午 10:16
 * @UpdateDate: 2023/5/6 11:24 AM
 * @UpdateRemark: 通过优化
 */
object SharedPreferencesHelper {
    /**
     * DES: 返回hook的SharedPreferences实例
     * 8.0以下 Reflect pending work finishers
     * 8.0以上 Reflect finishers PrivateApi
     */
    @SuppressLint("PrivateApi")
    @Suppress("UNCHECKED_CAST")
    fun getSharedPreferences(context: Context, name: String, mode: Int): SharedPreferences {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                val clz = Class.forName("android.app.QueuedWork")
                val field = clz.getDeclaredField("sPendingWorkFinishers")
                field.isAccessible = true
                val queue = field.get(clz) as? ConcurrentLinkedQueue<Runnable>
                if (queue != null) {
                    val proxy = ConcurrentLinkedQueueProxy(queue)
                    field.set(queue, proxy)
                }
            } else {
                val clz = Class.forName("android.app.QueuedWork")
                val field = clz.getDeclaredField("sFinishers")
                field.isAccessible = true
                val queue = field.get(clz) as? LinkedList<Runnable>
                if (queue != null) {
                    val linkedListProxy = LinkedListProxy(queue)
                    field.set(queue, linkedListProxy)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return context.getSharedPreferences(name, mode)
    }
}
