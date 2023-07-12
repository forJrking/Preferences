package com.forjrking.preferences

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.javaType
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
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> type() =
    if (T::class.isBasic) typeOf<Unit>().javaType else typeOf<T>().javaType

class ExampleUnitTest {
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun addition_isCorrect() {

        val writeTimeMillis3 = measureTimeMillis {
            repeat(1000) {
                assertNotNull(typeOf<Set<String>>().javaType)
            }
        }
        println("Time3: $writeTimeMillis3")
    }
}