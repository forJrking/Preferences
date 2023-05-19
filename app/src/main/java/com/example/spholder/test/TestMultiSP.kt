package com.example.spholder.test

import com.example.spholder.bo.Game
import com.forjrking.preferences.PreferencesOwner

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/2 10:48
 * @version: 1.0.0
 */
object TestMultiSP : PreferencesOwner(null, "123434334", false, true) {

    var testProcess by preferenceNullableBinding<String>()

    var testStr: String by preferenceBinding("multi")

    var testLong: Long by preferenceBinding(0L, "multiNumber")

    var game: Game? by preferenceNullableBinding(null)

}