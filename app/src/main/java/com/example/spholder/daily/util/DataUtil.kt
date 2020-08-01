package com.example.spholder.daily.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/22 13:36
 * @version: 1.0.0
 */
val format = "yyyyMMdd"

object DataUtil {

    //默认今天
    open fun day(times: Long = System.currentTimeMillis()): String {
        val df = SimpleDateFormat(format, Locale.US)
        return df.format(times)
    }

    //获取今天未来七天时间
    fun getSevenDay(): MutableList<String> {
        val days = mutableListOf<String>()
        repeat(7) {
            days.add(getFutureDate(it))
        }
        return days
    }

    /**
     * 获取未来 第 past 天的日期
     * @param past
     * @return
     */
    fun getFutureDate(past: Int): String {
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_YEAR] = calendar[Calendar.DAY_OF_YEAR] + past
        val times = calendar.time
        val df = SimpleDateFormat(format, Locale.US)
        return df.format(times)
    }
}

/**把 2020：03：20  转换为  20200320*/
fun String.toTime(): Long = this.trim().toLong()

