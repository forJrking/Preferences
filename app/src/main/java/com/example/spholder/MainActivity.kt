package com.example.spholder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import com.forjrking.preferences.kt.PreferenceHolder
import com.forjrking.preferences.proxy.SpRetrofit
import com.forjrking.preferences.serialize.GsonSerializer
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        MMKV.initialize(this)
        PreferenceHolder.context = this.applicationContext
        PreferenceHolder.serializer = GsonSerializer(Gson())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn1.setOnClickListener {

            val s1 = System.nanoTime()
            repeat(1000) {
                TestSP.testStr =  "BBBXXEEEE$it"
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

            val javaSP = SpRetrofit.create(this, JavaSP::class.java)

            val s1 = System.nanoTime()
            repeat(1000) {
//                javaSP.coin = "AAAXXEEEE0${Random().nextInt(20)}"
                TestmmkvSP.testStr = "AAAXXEEEE$it"
            }
            val s2 = System.nanoTime()
            println("mmkv set Time: ${TimeUnit.NANOSECONDS.toMillis(s2 - s1)}")

            repeat(1000) {
                val s = TestmmkvSP.testStr
//                val coin = javaSP.coin
            }
            val s3 = System.nanoTime()
            println("mmkv get Time: ${TimeUnit.NANOSECONDS.toMillis(s3 - s2)}")

            text.text = TestmmkvSP.testStr
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
