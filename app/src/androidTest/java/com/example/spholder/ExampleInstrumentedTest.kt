package com.example.spholder

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.spholder.test.TestSP
import com.forjrking.preferences.PreferencesOwner
import java.util.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis

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
        repeat(3) {
            val writeTimeMillis = measureTimeMillis {
                repeat(1000) {
                    val append = "AR:$it"
                    UTTestSP.stringCache = append
                }
            }
            println("set Time: $writeTimeMillis")

            val readTimeMillis = measureTimeMillis {
                repeat(1000) {
                    val case = UTTestSP.stringCache
                    val temp = case
                }
            }
            println("get Time: $readTimeMillis")
        }
    }

    @Test
    fun benchmarkForSpNormal() {
        TestSP.stringCase = "testStr"
    }
}

object UTTestSP : PreferencesOwner("sp-normal") {
    var stringCache: String by preferenceBinding("A", caching = true)
    var stringNoCache: String by preferenceBinding("A", caching = false)
}