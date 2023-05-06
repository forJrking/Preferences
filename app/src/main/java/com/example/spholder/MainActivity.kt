package com.example.spholder

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.spholder.bo.Game
import com.example.spholder.daily.TestActivity
import com.example.spholder.databinding.ActivityMainBinding
import com.example.spholder.test.TestCryptSP
import com.example.spholder.test.TestMultiSP
import com.example.spholder.test.TestSP
import com.example.spholder.test.TestmmkvSP
import com.forjrking.preferences.kt.PreferenceHolder
import com.forjrking.preferences.serialize.GsonSerializer
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        MMKV.initialize(this)
        PreferenceHolder.context = this.application
        PreferenceHolder.serializer = GsonSerializer(Gson())
        super.onCreate(savedInstanceState)
        val mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.btn1.setOnClickListener {
//            TestSP.clear()
//            TestSP.clearCache()
//            TestSP.testStr.log()
//            TestSP.testLong.toString().log()
//            TestSP.game.toString().log()
//            ////////////////////////////普通测试///////////////////////////
//            TestSP.testStr = "testStr"
//            TestSP.testLong = 100232L
//            ////////////////////////////序列化测试///////////////////////////
//            TestSP.game = Game(1,"sadasdsada",mutableListOf())
//
//
//            TestSP.testStr.log()
//            TestSP.testLong.toString().log()
//            TestSP.game.toString().log()

            //加密测试

            TestCryptSP.testStr.log()
            TestCryptSP.testLong.toString().log()
            TestCryptSP.game.toString().log()
            ////////////////////////////普通测试///////////////////////////
            TestCryptSP.testStr = "testStr"
            TestCryptSP._testStr2 = "_testStr2222"
            TestCryptSP.testLong = 100232L
            ////////////////////////////序列化测试///////////////////////////
            TestCryptSP.game = Game(1, "sadasdsada", mutableListOf())

            TestCryptSP.testStr.log()
            TestCryptSP._testStr2.log()
            TestCryptSP.testLong.toString().log()
            TestCryptSP.game.toString().log()

            TestCryptSP.getAll()?.forEach {
                Log.d("MainActivity", "TestCryptSP ->name:${it.key} value:${it.value}")
            }
            TestCryptSP.clear()
            TestCryptSP.getAll()?.forEach {
                Log.d("MainActivity", "TestCryptSP ->name:${it.key} value:${it.value}")
            }
        }



        mainBinding.btn2.setOnClickListener {
            TestmmkvSP.testStr.log()
            TestmmkvSP.coin.toString().log()

            TestmmkvSP.testStr = "multi Process test"
            TestmmkvSP.coin = 2000323
            TestmmkvSP.testStr.log()
            TestmmkvSP.coin.toString().log()
        }


        mainBinding.btn3.setOnClickListener {
            TestmmkvSP.testStr = "multi Process jump"
            TestmmkvSP.coin = 2998888

            TestMultiSP.testStr = "multi testStr"
            TestMultiSP.testLong = 199999L
            ////////////////////////////序列化测试///////////////////////////
            TestMultiSP.game = Game(91, "multistring", mutableListOf())
            TestMultiSP.getAll()?.forEach {
                Log.d("PreferenceHolder", "TestMultiSP ->name:${it.key} value:${it.value}")
            }
            startActivity(Intent(this@MainActivity, TestActivity::class.java))
        }


        /////////////////////////////性能测试///////////////////////
        val writeTimeMillis = measureTimeMillis {
            repeat(1000) {
                TestSP.testStr = "BBBXXEEEE$it"
            }
        }
        println("writeTimeMillis: $writeTimeMillis")

        val readTimeMillis = measureTimeMillis {
            repeat(1000) {
                val sp = TestSP.testStr
                val temp = sp
            }
        }
        println("readTimeMillis: $readTimeMillis")

        mainBinding.text.text =
            "writeTimeMillis: $writeTimeMillis \nreadTimeMillis: $readTimeMillis"
    }
}

fun String.log() {
    Log.d("MainActivity", this)
}