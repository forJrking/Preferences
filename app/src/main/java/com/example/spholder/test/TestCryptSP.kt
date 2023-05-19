package com.example.spholder.test

import com.example.spholder.bo.Game
import com.forjrking.preferences.PreferencesOwner

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/2 10:48
 * @version: 1.0.0
 */

interface Crypt {
    /**加密 失败返回值必须 null*/
    fun encrypt(text: String?): String?

    /**解密 失败返回值必须 null*/
    fun decrypt(cipherText: String?): String?
}

object TestCryptSP : PreferencesOwner(null, "Asdshajfsjk23432432", true) {

    var testStr: String by preferenceBinding("testStr-default")

    var testObj: Game? by preferenceNullableBinding()
}