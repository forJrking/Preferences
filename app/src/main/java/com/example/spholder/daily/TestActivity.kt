package com.example.spholder.daily

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.spholder.R
import com.example.spholder.bo.Game
import com.example.spholder.log
import com.example.spholder.test.TestCryptSP
import com.example.spholder.test.TestMultiSP
import com.example.spholder.test.TestmmkvSP
import com.forjrking.preferences.kt.PreferenceHolder
import com.forjrking.preferences.serialize.GsonSerializer
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_test.*


class TestActivity : AppCompatActivity() {

    private val controller by lazy { TaskOneController(this) }

    override fun onCreate(savedInstanceState: Bundle?) {

        PreferenceHolder.context = this.application
        PreferenceHolder.serializer = GsonSerializer(Gson())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        controller.setView(task1_ll)
        //设置下面的tip
        controller.setTip(task1_tip) {
            controller.setView(task1_ll)
        }

        TestmmkvSP.testStr.log()
        TestmmkvSP.coin.toString().log()
        TestmmkvSP.game = Game(1,"sadasdsada",mutableListOf())
        TestmmkvSP.getAll()?.forEach {
            Log.d("PreferenceHolder", "name:${it.key} value:${it.value}")
        }

//        TestMultiSP.testStr.log()
//        TestMultiSP.testLong.toString().log()
//        TestMultiSP.game.toString().log()
    }
}
