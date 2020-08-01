package com.example.spholder.daily

import com.example.spholder.bo.Time
import com.example.spholder.daily.util.DataUtil
import com.forjrking.preferences.kt.PreferenceHolder
import java.text.SimpleDateFormat
import java.util.*

/**
 * @description:
 * @author: 岛主
 * @date: 2020/8/1 14:57
 * @version: 1.0.0
 */
object ObjectTest : PreferenceHolder(null, null, false) {

    var tempStr: String by bindToPreferenceField("0","temp_${day()}")


    fun day(times: Long = System.currentTimeMillis()): String {
        val df = SimpleDateFormat("yyyyMMdd", Locale.US)
        return df.format(times)
    }

    val default: MutableList<Time> = mutableListOf<Time>().apply {
        add(Time(1, 0L))
        add(Time(2, 0L))
    }

    var times: MutableList<Time> by bindToPreferenceField(default)

    val dailyCoinDefault: MutableList<DailyReward>
        get() {
            val sevenDay = DataUtil.getSevenDay()
            return mutableListOf<DailyReward>().also {
                it.add(DailyReward(sevenDay[0], false))
                it.add(DailyReward(sevenDay[1], false))
                it.add(DailyReward(sevenDay[2], false))
                it.add(DailyReward(sevenDay[3], false))
                it.add(DailyReward(sevenDay[4], false))
                it.add(DailyReward(sevenDay[5], false))
                it.add(DailyReward(sevenDay[6], false))
            }
        }

    var taskDailyReward: MutableList<DailyReward> by bindToPreferenceField(dailyCoinDefault)
}