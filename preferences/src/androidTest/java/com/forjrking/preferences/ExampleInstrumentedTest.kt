package com.forjrking.preferences

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.forjrking.preferences.crypt.AesCrypt

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.concurrent.TimeUnit

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
        assertEquals("com.forjrking.preferences.test", appContext.packageName)
    }

    @Test
    fun crpty() {
        val aesCrypt = AesCrypt("12345678")
        val s1 = System.nanoTime()
        repeat(1000) {
            val encrypt = aesCrypt.encrypt("strinsjia$it")
            if(it == 1){
                println(encrypt)
            }
        }
        val s2 = System.nanoTime()
        println("encrypt Time: ${TimeUnit.NANOSECONDS.toMillis(s2 - s1)}")

        repeat(1000) {
            val encrypt =   aesCrypt.decrypt("Y9kx0jtRG7h7MOoF7BxdQg==")
            if(it == 1){
                println(encrypt)
            }
        }
        val s3 = System.nanoTime()
        println("decrypt Time: ${TimeUnit.NANOSECONDS.toMillis(s3 - s2)}")
    }

}