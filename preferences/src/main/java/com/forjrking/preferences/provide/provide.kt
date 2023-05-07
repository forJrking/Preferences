package com.forjrking.preferences.provide

import android.content.Context
import android.content.SharedPreferences
import android.util.Log


/**
 * @description:
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
        val mode = Context.MODE_PRIVATE
        if (isMultiProcess) {
            MultiProcessSharedPreferences.getSharedPreferences(this, xmlName, mode)
        } else {
            SharedPreferencesHelper.getSharedPreferences(this, xmlName, mode)
        }
    }
}