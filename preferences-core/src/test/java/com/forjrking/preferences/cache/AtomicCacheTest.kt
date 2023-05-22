package com.forjrking.preferences.cache

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test


/**
 * @description:
 * @author: forjrking
 * @date: 2023/5/21 20:48
 */
internal class AtomicCacheTest {

    private var flag: Int = 0

    @Before
    fun setUp() {
        flag = 0
    }

    private fun increase() {
        flag++
    }

    @Test
    fun `when double incept string then caching`() {
        val atomicCache = AtomicCache<String>(true)
        val value = "abc"
        atomicCache.incept(value, ::increase)
        assertEquals(1, flag)
        atomicCache.incept(value, ::increase)
        assertEquals(1, flag)
    }

    @Test
    fun `when double incept List then no caching`() {
        val atomicCache = AtomicCache<List<String>>(true)
        val value = mutableListOf("abc")
        atomicCache.incept(value, ::increase)
        assertEquals(value, atomicCache.acquire { emptyList() })
        assertEquals(1, flag)
        atomicCache.incept(listOf("abc"), ::increase)
        assertEquals(1, flag)
        atomicCache.incept(value.apply { add("def") }, ::increase)
        assertEquals(value, atomicCache.acquire { emptyList() })
        assertEquals(2, flag)
    }

    @Test
    fun `when double incept Data class then no caching`() {
        data class TestBo(var name: String, val list: MutableList<String>)

        val atomicCache = AtomicCache<TestBo>(true)
        val value = TestBo("abc", mutableListOf("abc"))
        atomicCache.incept(value, ::increase)
        assertEquals(1, flag)
        atomicCache.incept(TestBo("abc", mutableListOf("abc")), ::increase)
        assertEquals(1, flag)
        atomicCache.incept(value.apply {
            name = ("def")
            list.add("def")
        }, ::increase)
        assertEquals(2, flag)
    }

    @Test
    fun sameCaching() {
        val atomicCache = AtomicCache<String?>(true)
        atomicCache.incept(null, ::increase)
        assertTrue(atomicCache.sameCaching(null, null))
    }
}