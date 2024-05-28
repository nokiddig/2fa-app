package com.example.app_2fa.utils
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class Security {
    fun generateAESKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        return keyGen.generateKey()
    }

    fun encryptAES(plainText: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decryptAES(encryptedText: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText))
        return String(decryptedBytes)
    }

    fun generateDiffieHellmanKeyPair(): KeyPair {
        val keyPairGen = KeyPairGenerator.getInstance("DH")
        keyPairGen.initialize(2048) // Diffie-Hellman 2048-bit
        return keyPairGen.generateKeyPair()
    }

    fun generateSharedSecret(privateKey: PrivateKey, receivedPublicKey: ByteArray): ByteArray {
        val keyFactory = KeyFactory.getInstance("DH")
        val publicKeySpec = X509EncodedKeySpec(receivedPublicKey)
        val publicKey = keyFactory.generatePublic(publicKeySpec)

        val keyAgreement = KeyAgreement.getInstance("DH")
        keyAgreement.init(privateKey)
        keyAgreement.doPhase(publicKey, true)
        return keyAgreement.generateSecret()
    }

    // demo
    fun main(){
        //aes
        val secretKey = generateAESKey()
        val plainText = "Hello World, AES!"
        val encryptedText = encryptAES(plainText, secretKey)
        val decryptedText = decryptAES(encryptedText, secretKey)

        println("Original text: $plainText")
        println("Encrypted text: $encryptedText")
        println("Decrypted text: $decryptedText")

        //diffie hellman
        // Generate key pairs for both parties
        val keyPairA = generateDiffieHellmanKeyPair()
        val keyPairB = generateDiffieHellmanKeyPair()

        // Each party shares their public key with the other
        val publicKeyA = keyPairA.public.encoded
        val publicKeyB = keyPairB.public.encoded

        // Each party generates the shared secret
        val secretKeyA = generateSharedSecret(keyPairA.private, publicKeyB)
        val secretKeyB = generateSharedSecret(keyPairB.private, publicKeyA)

        println("Secret Key A: ${Base64.getEncoder().encodeToString(secretKeyA)}")
        println("Secret Key B: ${Base64.getEncoder().encodeToString(secretKeyB)}")
    }
}