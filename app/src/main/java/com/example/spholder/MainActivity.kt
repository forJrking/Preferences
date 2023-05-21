package com.example.spholder

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.example.spholder.daily.TestActivity
import com.example.spholder.databinding.ActivityMainBinding
import com.example.spholder.test.*
import com.forjrking.activity.library.launch4Result
import com.forjrking.preferences.PreferencesOwner
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    companion object {
        private var testCase: TestCase? = null
        private var preferencesOwner: PreferencesOwner? = null
        fun write() = testCase?.apply {
            intCase = 100
            longCase = 101L
            floatCase = 102F
            booleanCase = false
            stringCase = "123ABC-:/><;\"!##$%^&*"
            objCase = testObj.copy(numberId = 101, datas = null)
            setStringCase = setOf("NOT NULL")
            setObjCase = setOf(testObj.copy(numberId = 102, datas = null))
            setStringNullableCase = setOf("NULLABLE")
            setObjNullableCase = setOf(testObj.copy(numberId = 103, datas = null))
        }

        fun read(): String {
            return testCase?.run {
                StringBuffer()
                    .append("intCase:$intCase").appendLine()
                    .append("floatCase:$floatCase").appendLine()
                    .append("longCase:$longCase").appendLine()
                    .append("booleanCase:$booleanCase").appendLine()
                    .append("stringCase:$stringCase").appendLine()
                    .append("objCase:$objCase").appendLine()
                    .append("setStringCase:$setStringCase").appendLine()
                    .append("setObjCase:$setObjCase").appendLine()
                    .append("setStringNullableCase:$setStringNullableCase").appendLine()
                    .append("setObjNullableCase:$setObjNullableCase").appendLine().toString()
            } ?: ""
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        mainBinding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val sp = when (resources.getStringArray(R.array.enum_array)[position]) {
                    "SP-Normal" -> TestSP
                    "SP-Crypt" -> TestCryptSP
                    "SP-Multi" -> TestMultiSP
                    "MMKV-Normal" -> TestMMKV
                    "MMKV-Crypt" -> TestCryptMMKV
                    "MMKV-Multi" -> TestMultiMMKV
                    else -> TODO("not support")
                }
                testCase = sp
                preferencesOwner = sp
                preferencesOwner?.clearCache()
                preferencesOwner?.clear()
                mainBinding.text.apply {
                    text = "=======${sp::class.java.name}=======\n"
                    append(read())
                }
                write()
                mainBinding.text.apply {
                    append("========Override========\n")
                    append(read())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        mainBinding.processBtn.setOnClickListener {
            preferencesOwner?.getAll()?.forEach {
                Log.d("MainActivity", "-> name:${it.key} -> value:${it.value}")
            }
            val intent = Intent(this@MainActivity, TestActivity::class.java)
            this.launch4Result(intent, 201) { requestCode, resultCode, data ->
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        mainBinding.text.text = read()
                    }

                    else -> Unit
                }
            }
        }
        mainBinding.benchMarkBtn.setOnClickListener {
            /////////////////////////////性能测试///////////////////////
            val writeTimeMillis = measureTimeMillis {
                repeat(1000) {
                    write()
                }
            }
            println("writeTimeMillis: $writeTimeMillis")

            val readTimeMillis = measureTimeMillis {
                repeat(1000) {
                    read()
                }
            }
            println("readTimeMillis: $readTimeMillis")

            mainBinding.text.text =
                "writeTimeMillis: $writeTimeMillis \nreadTimeMillis: $readTimeMillis"
            preferencesOwner?.getAll()?.forEach {
                Log.d("MainActivity", "-> name:${it.key} -> value:${it.value}")
            }
        }
    }
}
