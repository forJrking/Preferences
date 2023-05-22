package com.example.spholder.daily

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import com.example.spholder.R
import com.example.spholder.bo.DailyReward
import com.example.spholder.daily.util.DataUtil
import com.example.spholder.daily.util.toTime

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/23 18:03
 * @version: 1.0.0
 */
class TaskOneController(val activity: AppCompatActivity) : LifecycleObserver {

    init {
        activity.lifecycle.addObserver(this)
    }

    var needSin: DailyReward? = null
    var items: MutableList<DailyReward>? = null
    var tempV: View? = null


    fun setView(view: ViewGroup) {
        view.removeAllViews()
        items = getData()
        val today = DataUtil.day()
        items!!.forEachIndexed { index, element ->
            val child = View.inflate(view.context, R.layout.task_item_one, null)
            val iv = child.findViewById<ImageView>(R.id.iv)
            val line = child.findViewById<View>(R.id.line)
            val textDay = child.findViewById<TextView>(R.id.tvday)
            if (element.dayIndex.toTime() == today.toTime()) {
                needSin = element
                tempV = textDay
            }
            if (index == 6) {
                line.visibility = View.GONE

            }
            if (element.isClmiaed) {
                iv.setImageResource(R.mipmap.task1_go)
                line.setBackgroundColor(Color.parseColor("#FFDC33"))
            } else {
                line.setBackgroundColor(Color.parseColor("#6D6D6D"))
                iv.setImageResource(R.mipmap.task1_goed)
            }
            textDay.text = "Day${index + 1}"
            view.addView(child)
        }
    }

    fun setTip(tip: View, action: () -> Unit) {
        tip.postDelayed({
            if (tempV != null) {
                var offset = IntArray(2)
                tempV!!.getLocationInWindow(offset)
                //设置tip的x坐标
                val fl = (tempV!!.width / 2f) + offset[0] - (tip.width / 2f)
                tip.getLocationInWindow(offset)
                tip.x = fl - offset[0]
            }
        }, 100)

        tip.setOnClickListener {
            if (needSin != null && needSin!!.isClmiaed) {
                //已经签到了
                Toast.makeText(activity, "Completed this task", Toast.LENGTH_SHORT).show()
            } else {
                val builder =
                    AlertDialog.Builder(activity).setTitle("签到").setPositiveButton("Ok") { _, _ ->
                        justDo()
                        action()
                    }.create()
                builder.show()
            }
        }
    }

    private fun justDo() {
        val today = DataUtil.day()
        //今天的奖励
//        val items = ObjectTest.taskDailyReward
        items?.forEach {
            if (it.dayIndex.toTime() == today.toTime()) {
                //今天要签到的数据
                it.isClmiaed = true
            }
        }
        ObjectTest.taskDailyReward = items!!
    }

    private fun getData(): MutableList<DailyReward> {
        var dailyReward = ObjectTest.taskDailyReward
        //到今天之前全部时间必须签到 否则重置
        val today = DataUtil.day()
        //最后一天已经比现在小
        val lastDay = dailyReward.last()
        if (today.toTime() > lastDay.dayIndex.toTime()) {
            dailyReward = ObjectTest.dailyCoinDefault
        } else if (lastDay.dayIndex.toTime() > DataUtil.getFutureDate(6).toTime()) {
            //用户乱修改时间之前任务已经超出此7天
            dailyReward = ObjectTest.dailyCoinDefault
        } else {
            //今天之前的必须全部签到
            val filter = dailyReward.filter { it.dayIndex.toTime() < today.toTime() }
            if (filter.isNotEmpty()) {
                filter.forEach {
                    if (!it.isClmiaed) {
                        dailyReward = ObjectTest.dailyCoinDefault
                        return@forEach
                    }
                }
            }
        }
        //重新注入数据
        ObjectTest.taskDailyReward = dailyReward
        return dailyReward
    }

}