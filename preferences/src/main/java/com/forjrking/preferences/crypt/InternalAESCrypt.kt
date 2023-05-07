package com.forjrking.preferences.crypt

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES 实现的加解密工具  性能不够优秀建议使用C++
 * */
internal class InternalAESCrypt(private val psd: String) : Crypt {

    private val AES_MODE = "AES/CBC/PKCS5Padding" // PKCS7Padding 性能竟然是前者2倍 搞不懂。。
    private val HASH_ALGORITHM = "SHA-256"
    private val CHARSET = "UTF-8"
    private val CIPHER = "AES"

    private val IV_BYTES = byteArrayOf(
        0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
        0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00
    )

    private val key: SecretKeySpec by lazy { generateKey(psd) }

    /**
     * Generates SHA256 hash of the password which is used as key
     *
     * @param password used to generated key
     * @return SHA256 of the password
     */
    @Throws(
        NoSuchAlgorithmException::class,
        UnsupportedEncodingException::class,
        IllegalArgumentException::class
    )
    private fun generateKey(password: String): SecretKeySpec {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        val bytes = password.toByteArray(charset(CHARSET))
        digest.update(bytes, 0, bytes.size)
        val key = digest.digest()
        return SecretKeySpec(key, CIPHER)
    }

    /**
     * Encrypt and encode message using 256-bit AES with key generated from password.
     *
     * @param password used to generated key
     * @param message  the thing you want to encrypt assumed String UTF-8
     * @return Base64 encoded CipherText
     * @throws GeneralSecurityException if problems occur during encryption
     */
    override fun encrypt(message: String?): String? {
        if (message.isNullOrEmpty()) return null
        return try {
            val cipherText = encrypt(key, IV_BYTES, message.toByteArray(charset(CHARSET)))
            //NO_WRAP is important as was getting \n at the end
            Base64.encodeToString(cipherText, Base64.NO_WRAP)
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }


    /**
     * More flexible AES encrypt that doesn't encode
     *
     * @param key     AES key typically 128, 192 or 256 bit
     * @param iv      Initiation Vector
     * @param message in bytes (assumed it's already been decoded)
     * @return Encrypted cipher text (not encoded)
     * @throws Exception if something goes wrong during encryption
     */
    @Throws(Exception::class)
    fun encrypt(key: SecretKeySpec, iv: ByteArray, message: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(AES_MODE)
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
        return cipher.doFinal(message)
    }


    /**
     * Decrypt and decode ciphertext using 256-bit AES with key generated from password
     *
     * @param password                used to generated key
     * @param cipherText the encrpyted message encoded with base64
     * @return message in Plain text (String UTF-8)
     * @throws GeneralSecurityException if there's an issue decrypting
     */
    override fun decrypt(cipherText: String?): String? {
        if (cipherText.isNullOrEmpty()) return null
        return try {
            val decodedCipherText = Base64.decode(cipherText, Base64.NO_WRAP)
            val decryptedBytes = decrypt(key, IV_BYTES, decodedCipherText)
            String(decryptedBytes, charset(CHARSET))
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }

    /**
     * More flexible AES decrypt that doesn't encode
     *
     * @param key               AES key typically 128, 192 or 256 bit
     * @param iv                Initiation Vector
     * @param decodedCipherText in bytes (assumed it's already been decoded)
     * @return Decrypted message cipher text (not encoded)
     * @throws Exception if something goes wrong during encryption
     */
    @Throws(Exception::class)
    fun decrypt(key: SecretKeySpec, iv: ByteArray, decodedCipherText: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(AES_MODE)
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
        return cipher.doFinal(decodedCipherText)
    }

}