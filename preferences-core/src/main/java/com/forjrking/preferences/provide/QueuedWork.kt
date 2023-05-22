package com.forjrking.preferences.provide

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * 在8.0以下代理
 * The set of Runnables that will finish or wait on any async activities started by the application.
 * private static final ConcurrentLinkedQueue<Runnable> sPendingWorkFinishers = new ConcurrentLinkedQueue<Runnable>();
 */

internal class ConcurrentLinkedQueueProxy(private val sPendingWorkFinishers: ConcurrentLinkedQueue<Runnable>) :
    ConcurrentLinkedQueue<Runnable>() {

    override fun add(element: Runnable?): Boolean = sPendingWorkFinishers.add(element)

    override fun remove(element: Runnable?): Boolean = sPendingWorkFinishers.remove(element)

    override fun isEmpty(): Boolean = true

    /**
     * 代理的poll()方法，永远返回空，这样UI线程就可以避免被阻塞，继续执行了
     */
    override fun poll(): Runnable? = null
}

/**
 * 在8.0以上apply()中QueuedWork.addFinisher(awaitCommit), 需要代理的是LinkedList，如下：
 * # private static final LinkedList<Runnable> sFinishers = new LinkedList<>()
 */
internal class LinkedListProxy(private val sFinishers: LinkedList<Runnable>) :
    LinkedList<Runnable>() {

    override fun add(element: Runnable): Boolean = sFinishers.add(element)

    override fun remove(element: Runnable): Boolean = sFinishers.remove(element)

    override fun isEmpty(): Boolean = true

    /**
     * 代理的poll()方法，永远返回空，这样UI线程就可以避免被阻塞，继续执行了
     */
    override fun poll(): Runnable? = null
}
