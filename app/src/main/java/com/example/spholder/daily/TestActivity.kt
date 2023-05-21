package com.example.spholder.daily

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spholder.databinding.ActivityTestBinding
import com.forjrking.preferences.PreferencesOwner
import com.forjrking.preferences.serialize.GsonSerializer
import com.google.gson.GsonBuilder


class TestActivity : AppCompatActivity() {

    private val controller by lazy { TaskOneController(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        PreferencesOwner.context = this.applicationContext
        PreferencesOwner.serializer = GsonSerializer(GsonBuilder().serializeNulls().create())
        super.onCreate(savedInstanceState)
        val testBinding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(testBinding.root)
        controller.setView(testBinding.task1Ll)
        //设置下面的tip
        controller.setTip(testBinding.task1Tip) {
            controller.setView(testBinding.task1Ll)
        }

        testBinding.processButton.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
