package com.example.spholder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.spholder.bo.Game
import com.example.spholder.daily.TestActivity
import com.example.spholder.test.TestCryptSP
import com.example.spholder.test.TestMultiSP
import com.example.spholder.test.TestSP
import com.example.spholder.test.TestmmkvSP
import com.forjrking.preferences.kt.PreferenceHolder
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
            TestSP.clear()
            TestSP.clearCache()
            TestSP.testStr.log()
            TestSP.testLong.toString().log()
            TestSP.game.toString().log()
            ////////////////////////////普通测试///////////////////////////
            TestSP.testStr = "testStr"
            TestSP.testLong = 100232L
            ////////////////////////////序列化测试///////////////////////////
            TestSP.game = Game(1,"sadasdsada",mutableListOf())


            TestSP.testStr.log()
            TestSP.testLong.toString().log()
            TestSP.game.toString().log()

            //加密测试

            TestCryptSP.testStr.log()
            TestCryptSP.testLong.toString().log()
            TestCryptSP.game.toString().log()
            ////////////////////////////普通测试///////////////////////////
            TestCryptSP.testStr = "testStr"
            TestCryptSP.testLong = 100232L
            ////////////////////////////序列化测试///////////////////////////
            TestCryptSP.game = Game(1,"sadasdsada",mutableListOf())

            TestCryptSP.testStr.log()
            TestCryptSP.testLong.toString().log()
            TestCryptSP.game.toString().log()

            TestCryptSP.getAll()?.forEach {
                Log.d("PreferenceHolder", "TestCryptSP ->name:${it.key} value:${it.value}")
            }
        }



        btn2.setOnClickListener {
            TestmmkvSP.testStr.log()
            TestmmkvSP.coin.toString().log()

            TestmmkvSP.testStr = "multi Process test"
            TestmmkvSP.coin = 2000323
            TestmmkvSP.testStr.log()
            TestmmkvSP.coin.toString().log()
        }


        btn3.setOnClickListener {
            TestmmkvSP.testStr = "multi Process jump"
            TestmmkvSP.coin = 2998888

            TestMultiSP.testStr = "multi testStr"
            TestMultiSP.testLong = 199999L
            ////////////////////////////序列化测试///////////////////////////
            TestMultiSP.game = Game(91,"multistring",mutableListOf())
            TestMultiSP.getAll()?.forEach {
                Log.d("PreferenceHolder", "TestMultiSP ->name:${it.key} value:${it.value}")
            }
            startActivity(Intent(this@MainActivity,TestActivity::class.java))
        }


        /////////////////////////////性能测试///////////////////////
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
}
fun String.log(){
    Log.d("MainActivity",this)
}