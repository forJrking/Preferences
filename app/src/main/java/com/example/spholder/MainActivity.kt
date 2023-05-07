package com.example.spholder

import android.annotation.SuppressLint
import android.app.Activity
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
import com.forjrking.activity.library.launch4Result
import com.forjrking.preferences.PreferencesOwner
import com.forjrking.preferences.serialize.GsonSerializer
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        MMKV.initialize(this)
        PreferencesOwner.context = this.application
        PreferencesOwner.serializer = GsonSerializer(Gson())
        super.onCreate(savedInstanceState)
        val mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.spBtn.setOnClickListener {
            TestCryptSP.testStr.log()
            TestCryptSP.testObj.log()
            ////////////////////////////普通测试///////////////////////////
            TestCryptSP.testStr = "testStr"
            TestCryptSP.testStr.log()
//            ////////////////////////////序列化测试///////////////////////////
//            TestSP.clearCache()
//            var game = Game(1, "multi", mutableListOf())
//            TestSP.game = game
//            game = TestSP.game!!
//            game.log()
//            game.numberId = 2
//            TestSP.game = game
//            TestSP.game.log()
//            ObjectTest.clearCache()
//            val dailyReward = ObjectTest.taskDailyReward
//            dailyReward.log()
//            ObjectTest.taskDailyReward = dailyReward
//            dailyReward.forEach {
//                it.isClmiaed = true
//            }
//            ObjectTest.taskDailyReward = dailyReward
//            ObjectTest.taskDailyReward.log()
            //加密测试

//            TestCryptSP.testStr.log()
//            TestCryptSP.testLong.log()
//            TestCryptSP.game.log()
//            ////////////////////////////普通测试///////////////////////////
//            TestCryptSP.testStr = "testStr"
//            TestCryptSP._testStr2 = "_testStr2222"
//            TestCryptSP.testLong = 100232L
//            ////////////////////////////序列化测试///////////////////////////
//            TestCryptSP.game = Game(1, "sadasdsada", mutableListOf())
//
//            TestCryptSP.testStr.log()
//            TestCryptSP._testStr2.log()
//            TestCryptSP.testLong.log()
//            TestCryptSP.game.log()
//
//            TestCryptSP.getAll()?.forEach {
//                Log.d("MainActivity", "TestCryptSP ->name:${it.key} value:${it.value}")
//            }
//            TestCryptSP.clear()
//            TestCryptSP.getAll()?.forEach {
//                Log.d("MainActivity", "TestCryptSP ->name:${it.key} value:${it.value}")
//            }
        }

        mainBinding.mmkvBtn.setOnClickListener {
            TestmmkvSP.testStr.log()
            TestmmkvSP.testNumber.log()

            TestmmkvSP.testStr = "mmkv test"
            TestmmkvSP.testNumber = 2000323
            TestmmkvSP.testStr.log()
            TestmmkvSP.testNumber.log()
        }

        mainBinding.processBtn.setOnClickListener {
            TestmmkvSP.testStr = "multi Process jump"
            TestmmkvSP.testNumber = 2998888

            TestMultiSP.testStr = "multi testStr"
            TestMultiSP.testLong = 199999L
            ////////////////////////////序列化测试///////////////////////////
            TestMultiSP.game = Game(91, "multistring", mutableListOf())
            TestMultiSP.getAll()?.forEach {
                Log.d("PreferenceHolder", "TestMultiSP ->name:${it.key} value:${it.value}")
            }

            TestMultiSP.testProcess = "main"
            TestmmkvSP.testProcess = "main"
            val intent = Intent(this@MainActivity, TestActivity::class.java)
            this.launch4Result(intent, 201) { _, resultCode, _ ->
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        mainBinding.text.text =
                            "SP:${TestMultiSP.testProcess}, MMKV:${TestmmkvSP.testProcess}"
                        TestMultiSP.testProcess?.log()
                        TestmmkvSP.testProcess?.log()
                    }

                    else -> {}
                }
            }
        }
        mainBinding.benchMarkBtn.setOnClickListener {
            /////////////////////////////性能测试///////////////////////
            val writeTimeMillis = measureTimeMillis {
                repeat(1000) {
                    TestSP.testStr = "BXE$it"
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
}

fun Any?.log() {
    Log.w("MainActivity", "$this")
}