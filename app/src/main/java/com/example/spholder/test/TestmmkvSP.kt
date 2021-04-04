package com.example.spholder.test

import com.example.spholder.bo.Game
import com.forjrking.preferences.kt.PreferenceHolder

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/2 10:48
 * @version: 1.0.0
 */
object TestmmkvSP : PreferenceHolder(null, "12345678", true, true) {

    var testStr: String by bindToPreferenceField("")

    var coin: Long by bindToPreferenceField(0L)

    var canNull: List<String>? by bindToPreferenceFieldNullable()

    var game: Game? by bindToPreferenceFieldNullable()

}