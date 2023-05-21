package com.forjrking.preferences.provide

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue


/**
 * DES: 返回hook的SharedPreferences实例
 * 8.0以下 Reflect pending work finishers
 * 8.0以上 Reflect finishers PrivateApi
 * @author: 岛主
 * @date: 2020/7/21 11:21
 * @version: 1.0.0
 * 生成支持多进程的mmkv
 * @param name xml名称  默认包名，建议给名字否则出现操作同key问题
 * @param cryptKey 加密密钥 mmkv加密密钥 SharedPreferences 内部方法不支持加密
 * @param isMMKV  是否使用mmkv
 * @param isMultiProcess 是否使用多进程  建议mmkv搭配使用 sp性能很差
 *
 * 此方法不提供MMKV初始化需要自己操作配置
 */
@JvmOverloads
@SuppressLint("PrivateApi")
@Suppress("UNCHECKED_CAST")
fun Context.createSharedPreferences(
    name: String? = null,
    cryptKey: String? = null,
    isMultiProcess: Boolean = false,
    isMMKV: Boolean = false
): SharedPreferences {
    val xmlName = "${if (name.isNullOrEmpty()) packageName else name}_kv"
    return if (isMMKV) {
        if (com.tencent.mmkv.MMKV.getRootDir().isNullOrEmpty()) {
            Log.e("MMKV", "You forgot to initialize MMKV")
            com.tencent.mmkv.MMKV.initialize(this)
        }
        val mode = if (isMultiProcess) com.tencent.mmkv.MMKV.MULTI_PROCESS_MODE
        else com.tencent.mmkv.MMKV.SINGLE_PROCESS_MODE
        com.tencent.mmkv.MMKV.mmkvWithID(xmlName, mode, cryptKey)
    } else {
        run {
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
            return if (isMultiProcess) {
                MultiProcessSharedPreferences.getSharedPreferences(this, xmlName, cryptKey)
            } else {
                compatSharedPreferences(this, xmlName, cryptKey)
            }
        }
    }
}

internal fun compatSharedPreferences(
    context: Context,
    name: String,
    keyAlias: String?
): SharedPreferences {
    return if (keyAlias.isNullOrEmpty()) {
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
    } else {
        val buildAES256GCMKeyGenParameterSpec = KeyGenParameterSpec.Builder(
            keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        androidx.security.crypto.EncryptedSharedPreferences.create(
            name,
            androidx.security.crypto.MasterKeys.getOrCreate(buildAES256GCMKeyGenParameterSpec),
            context,
            androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}
