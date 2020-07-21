package com.forjrking.preferences.crypt


interface Crypt {
    /**加密*/
    fun encrypt(message: String?): String?
    /**解密*/
    fun decrypt(base64CipherText: String?): String?
}
