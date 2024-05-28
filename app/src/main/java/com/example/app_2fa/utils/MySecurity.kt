package com.example.app_2fa.utils

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class MySecurity(val secretkey:String = "tuanhuysy"){
    fun hashKey(key: String): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(key.toByteArray(Charsets.UTF_8))
    }

    fun encrypt(data: String, key: ByteArray = hashKey(secretkey)): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding") // PKCS5 là PKCS7 cho các khối 8 byte
        val secretKeySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    fun decrypt(data: String, key: ByteArray = hashKey(secretkey)): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding") // PKCS5 là PKCS7 cho các khối 8 byte
        val secretKeySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val encryptedBytes = Base64.decode(data, Base64.DEFAULT)
        val decrypted = cipher.doFinal(encryptedBytes)
        return String(decrypted, Charsets.UTF_8)
    }
}