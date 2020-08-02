package com.example.spholder.test

import com.example.spholder.bo.Game
import com.forjrking.preferences.kt.PreferenceHolder

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/2 10:48
 * @version: 1.0.0
 */
object TestCryptSP : PreferenceHolder(null, "Asdshajfsjk23432432") {

    var testStr: String by bindToPreferenceField("")

    var testLong: Long by bindToPreferenceField(0L,"Number")

    var game: Game? by bindToPreferenceFieldNullable()

}