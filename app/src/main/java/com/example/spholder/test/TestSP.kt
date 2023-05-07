package com.example.spholder.test

import com.example.spholder.bo.Game
import com.forjrking.preferences.PreferencesOwner

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/2 10:48
 * @version: 1.0.0
 */
object TestSP : PreferencesOwner("test", "") {

    var testStr: String by bindToPreferenceField("testStr-default")

    var testObj: Game? by bindToPreferenceFieldNullable()

}