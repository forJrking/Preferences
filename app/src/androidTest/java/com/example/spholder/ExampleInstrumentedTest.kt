package com.example.spholder

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.spholder.test.TestSP
import java.util.*
import java.util.concurrent.TimeUnit
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.spholder", appContext.packageName)
    }


    @Test
    fun benchmark() {

        val s1 = System.nanoTime()
        repeat(1) {
            TestSP.testStr = "AAAXXEEEE0${Random().nextInt(it)}"
        }
        val s2 = System.nanoTime()
        println("set Time: ${TimeUnit.NANOSECONDS.toMillis(s2 - s1)}")

        repeat(1) {
            val s = TestSP.testStr
        }
        val s3 = System.nanoTime()
        println("get Time: ${TimeUnit.NANOSECONDS.toMillis(s3 - s2)}")
    }

    @Test
    fun benchmarkForSpNormal() {
        TestSP.testStr.log()
        TestSP.testObj.log()
        TestSP.testStr = "testStr"
        TestSP.testStr.log()
    }
}