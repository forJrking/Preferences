package com.example.spholder.test

import com.example.spholder.bo.Game
import com.forjrking.preferences.crypt.Crypt
import com.forjrking.preferences.PreferencesOwner

/**
 * @description:
 * @author: 岛主
 * @date: 2020/7/2 10:48
 * @version: 1.0.0
 */
object TestCryptSP : PreferencesOwner(null, "Asdshajfsjk23432432", true) {

    init {
        //自定义加密接口
        crypt = object : Crypt {
            override fun encrypt(text: String?): String? {
                return ""
            }

            override fun decrypt(cipherText: String?): String? {
                return ""
            }
        }
    }

    var testStr: String by preferenceBinding("testStr-default")

    var testObj: Game? by preferenceNullableBinding()
}