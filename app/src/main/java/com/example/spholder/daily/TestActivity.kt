package com.example.spholder.daily

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.spholder.bo.Game
import com.example.spholder.databinding.ActivityTestBinding
import com.example.spholder.log
import com.example.spholder.test.TestMultiSP
import com.example.spholder.test.TestmmkvSP
import com.forjrking.preferences.PreferencesOwner
import com.forjrking.preferences.serialize.GsonSerializer
import com.google.gson.Gson


class TestActivity : AppCompatActivity() {

    private val controller by lazy { TaskOneController(this) }

    override fun onCreate(savedInstanceState: Bundle?) {

        PreferencesOwner.context = this.application
        PreferencesOwner.serializer = GsonSerializer(Gson())
        super.onCreate(savedInstanceState)
        val testBinding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(testBinding.root)
        controller.setView(testBinding.task1Ll)
        //设置下面的tip
        controller.setTip(testBinding.task1Tip) {
            controller.setView(testBinding.task1Ll)
        }

        TestmmkvSP.testStr.log()
        TestmmkvSP.testNumber.toString().log()
        TestmmkvSP.testObj = Game(1, "sad", mutableListOf())
        TestmmkvSP.getAll()?.forEach {
            Log.d("PreferenceHolder", "name:${it.key} value:${it.value}")
        }

//        TestMultiSP.testStr.log()
//        TestMultiSP.testLong.toString().log()
//        TestMultiSP.game.toString().log()
        testBinding.processButton.setOnClickListener {
            TestMultiSP.testProcess = "testing"
            TestmmkvSP.testProcess = "testing"
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
