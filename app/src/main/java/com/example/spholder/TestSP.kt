package com.example.spholder

import com.forjrking.preferences.kt.PreferenceHolder

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/2 10:48
 * @version: 1.0.0
 */
object TestSP : PreferenceHolder("test", "12345678") {

    var testStr: String by bindToPreferenceField("", null, false)

    var coin: Long by bindToPreferenceField(0L)

    var tes: String? by bindToPreferenceFieldNullable()

    var game: Game? by bindToPreferenceFieldNullable()

}