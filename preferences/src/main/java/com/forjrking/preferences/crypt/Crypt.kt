package com.forjrking.preferences.crypt


interface Crypt {

    //加密密钥 建议放入构造方法中初始化
    val psd: String

    /**加密 失败返回值必须 null*/
    fun encrypt(text: String?): String?

    /**解密 失败返回值必须 null*/
    fun decrypt(cipherText: String?): String?
}
