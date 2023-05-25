package com.example.spholder

import com.example.spholder.test.TestSP
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testString() {
        val qualifiedName = TestSP::class.simpleName?.lowercase()
        val str = qualifiedName?.split(".", limit = 2)
        println("======================= $str ==========================")
    }
}