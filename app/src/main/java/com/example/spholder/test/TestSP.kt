package com.example.spholder.test

import com.example.spholder.bo.Game
import com.forjrking.preferences.PreferencesOwner

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/2 10:48
 * @version: 1.0.0
 */
object TestSP : PreferencesOwner("sp-normal"), TestCase {

    override var intCase: Int by preferenceBinding(0)

    override var floatCase: Float by preferenceBinding(1F)

    override var longCase: Long by preferenceBinding(2L)

    override var booleanCase: Boolean by preferenceBinding(true)

    override var stringCase: String by preferenceBinding("ABC")

    override var setStringCase: Set<String> by preferenceBinding(setOf("S", "E", "T"))

    override var setObjCase: Set<Game> by preferenceBinding(setOf(testObj))

    override var objCase: Game by preferenceBinding(testObj)

    override var setStringNullableCase: Set<String>? by preferenceNullableBinding()

    override var setObjNullableCase: Set<Game>? by preferenceNullableBinding()
}

object TestCryptSP : PreferencesOwner(cryptKey = "123456"), TestCase {

    override var intCase: Int by preferenceBinding(0)

    override var floatCase: Float by preferenceBinding(1F)

    override var longCase: Long by preferenceBinding(2L)

    override var booleanCase: Boolean by preferenceBinding(true)

    override var stringCase: String by preferenceBinding("ABC")

    override var setStringCase: Set<String> by preferenceBinding(setOf("S", "E", "T"))

    override var setObjCase: Set<Game> by preferenceBinding(setOf(testObj))

    override var objCase: Game by preferenceBinding(testObj)

    override var setStringNullableCase: Set<String>? by preferenceNullableBinding()

    override var setObjNullableCase: Set<Game>? by preferenceNullableBinding()
}

object TestMultiSP : PreferencesOwner(isMultiProcess = true), TestCase {

    override var intCase: Int by preferenceBinding(0)

    override var floatCase: Float by preferenceBinding(1F)

    override var longCase: Long by preferenceBinding(2L)

    override var booleanCase: Boolean by preferenceBinding(true)

    override var stringCase: String by preferenceBinding("ABC")

    override var setStringCase: Set<String> by preferenceBinding(setOf("S", "E", "T"))

    override var setObjCase: Set<Game> by preferenceBinding(setOf(testObj))

    override var objCase: Game by preferenceBinding(testObj)

    override var setStringNullableCase: Set<String>? by preferenceNullableBinding()

    override var setObjNullableCase: Set<Game>? by preferenceNullableBinding()
}
