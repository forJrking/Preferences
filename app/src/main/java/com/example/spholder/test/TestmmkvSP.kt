package com.example.spholder.test

import com.example.spholder.bo.Game
import com.forjrking.preferences.PreferencesOwner

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/2 10:48
 * @version: 1.0.0
 */
object TestmmkvSP : PreferencesOwner(null, "12345678", true, true) {

    var testStr: String by bindToPreferenceField("")

    var testNumber: Long by bindToPreferenceField(0L)

    var testProcess by bindToPreferenceFieldNullable<String>()

    var testObj: Game? by bindToPreferenceFieldNullable()

}