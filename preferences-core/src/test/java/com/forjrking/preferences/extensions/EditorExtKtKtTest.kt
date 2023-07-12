package com.forjrking.preferences.extensions

import android.content.SharedPreferences
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

/**
 * @description:
 * @author: forjrking
 * @date: 2023/5/22 15:46
 */
internal class EditorExtKtKtTest {

    private val UNIT_TYPE = typeOf<Unit>().javaType

    private lateinit var mockSP: SharedPreferences

    private val key = "key"

    @BeforeEach
    fun setUp() {
        mockSP = mock() {
            val mockEditor = mock<SharedPreferences.Editor>()
            on { edit() } doReturn mockEditor
            on { getInt(eq(key), anyOrNull()) } doReturn 1
            on { getFloat(eq(key), anyOrNull()) } doReturn 1F
            on { getLong(eq(key), anyOrNull()) } doReturn 1L
            on { getBoolean(eq(key), anyOrNull()) } doReturn true
            on { getString(eq(key), anyOrNull()) } doReturn "ABC"
            on { getStringSet(eq(key), anyOrNull()) } doReturn null
        }
    }


    @Test
    fun putValue() {
        mockSP.edit().putValue(Int::class, UNIT_TYPE, key, 0)
        mockSP.edit().putValue(Float::class, UNIT_TYPE, key, 1F)
        mockSP.edit().putValue(Long::class, UNIT_TYPE, key, 2L)
    }

    @Test
    fun getValue() {
        var value = mockSP.getValue(Int::class, UNIT_TYPE, key, 0)
       assertEquals(1, value)
        value = mockSP.getValue(Float::class, UNIT_TYPE, key, 0F)
       assertEquals(1F, value)
        value = mockSP.getValue(Long::class, UNIT_TYPE, key, 0L)
       assertEquals(1L, value)
        value = mockSP.getValue(String::class, UNIT_TYPE, key, null)
       assertEquals("ABC", value)
    }

    @Test
    fun getValueObj() {
        val value = mockSP.getValue(Set::class, typeOf<Set<String>>().javaType, key, null)
       assertEquals(null, value)
    }

    @Test
    fun `getValueObj not support`() {
        val value = mockSP.getValue(Set::class, typeOf<Set<Long>>().javaType, key, null)
       assertEquals(null, value)
    }
}