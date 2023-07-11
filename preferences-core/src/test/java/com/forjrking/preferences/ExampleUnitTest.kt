package com.forjrking.preferences

import com.forjrking.preferences.serialize.TypeToken
import org.junit.Assert.assertNotNull
import org.junit.Test
import kotlin.reflect.KClass
import kotlin.reflect.typeOf
import kotlin.system.measureTimeMillis

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
val KClass<*>.isBasic
    get() = when (this) {
        Int::class, Float::class, Long::class, Boolean::class, String::class -> true
        else -> false
    }

/**
 * sp需要序列化时候传递 type
 */
inline fun <reified T> type() =
    if (T::class.isBasic) object : TypeToken<Unit>() {}.javaType else object : TypeToken<T>() {}.javaType

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val writeTimeMillis = measureTimeMillis {
            repeat(1000) {
                assertNotNull(type<Set<String>>())
            }
        }
        val writeTimeMillis2 = measureTimeMillis {
            repeat(1000) {
                assertNotNull(object : TypeToken<Set<String>>() {}.javaType)
            }
        }
        val writeTimeMillis3 = measureTimeMillis {
            repeat(1000) {
                assertNotNull(typeOf<Set<String>>())
            }
        }
        println("Time1: $writeTimeMillis Time2: $writeTimeMillis2 Time3: $writeTimeMillis3")
    }
}