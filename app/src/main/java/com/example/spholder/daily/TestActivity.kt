package com.example.spholder.daily

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.spholder.R
import com.example.spholder.bo.Game
import com.example.spholder.databinding.ActivityTestBinding
import com.example.spholder.log
import com.example.spholder.test.TestmmkvSP
import com.forjrking.preferences.kt.PreferenceHolder
import com.forjrking.preferences.serialize.GsonSerializer
import com.google.gson.Gson


class TestActivity : AppCompatActivity() {

    private val controller by lazy { TaskOneController(this) }

    override fun onCreate(savedInstanceState: Bundle?) {

        PreferenceHolder.context = this.application
        PreferenceHolder.serializer = GsonSerializer(Gson())
        super.onCreate(savedInstanceState)
        val testBinding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(testBinding.root)
        controller.setView(testBinding.task1Ll)
        //设置下面的tip
        controller.setTip(testBinding.task1Tip) {
            controller.setView(testBinding.task1Ll)
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
