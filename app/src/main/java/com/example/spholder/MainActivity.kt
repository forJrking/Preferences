package com.example.spholder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.spholder.bo.Game
import com.example.spholder.daily.ObjectTest
import com.example.spholder.daily.util.DataUtil
import com.example.spholder.daily.util.toTime
import com.forjrking.preferences.kt.PreferenceHolder
import com.forjrking.preferences.proxy.SpRetrofit
import com.forjrking.preferences.serialize.GsonSerializer
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        MMKV.initialize(this)
        PreferenceHolder.context = this.application
        PreferenceHolder.serializer = GsonSerializer(Gson())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn1.setOnClickListener {

            val s1 = System.nanoTime()
            repeat(1000) {
                TestSP.testStr = "BBBXXEEEE$it"
            }
            val s2 = System.nanoTime()
            println("set Time: ${TimeUnit.NANOSECONDS.toMillis(s2 - s1)}")

            repeat(1000) {
                val s = TestSP.testStr
            }
            val s3 = System.nanoTime()
            println("get Time: ${TimeUnit.NANOSECONDS.toMillis(s3 - s2)}")

            text.text = TestSP.testStr
        }



        btn2.setOnClickListener {
            val items = ObjectTest.taskDailyReward
            ObjectTest.taskDailyReward = items

            items.forEach {
                //今天要签到的数据
                it.isClmiaed = true
            }
            ObjectTest.taskDailyReward = items
        }

        println(TestSP.game?.toString())

        TestSP.game = Game().also {
            it.numbeId = 1
            it.path = "sadasdsada"
            it.datas = mutableListOf()
        }

        println(TestSP.game?.toString())
//        javaSP.coin = "212"
//        val coin = javaSP.coin
//        val firstShare = javaSP.isFirstShare(true)
//        javaSP.setFirstShare(false)


    }
}
