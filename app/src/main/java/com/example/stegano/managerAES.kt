package com.example.stegano

import android.security.keystore.KeyProperties
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

open class CryptoManagerAES {

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        const val HASH_SIZE = 32
    }   // Parameters for the cryptographic cipher

    private lateinit var iv: ByteArray
    private var passHash: ByteArray? = null

    private fun getEncryptionCipher(keyGenerateSeed: String): Cipher{
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, createKey(keyGenerateSeed))
        }
    }

    private fun getDecryptionCipher(iv: ByteArray, keyGenerateSeed: String): Cipher{
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, createKey(keyGenerateSeed), IvParameterSpec(iv))
        }
    }

    private fun generatePassHash(keyGenerateSeed: String): ByteArray{
        val digest = MessageDigest.getInstance("SHA-256")
        val seedBytes = keyGenerateSeed.toByteArray(Charsets.UTF_8)
        passHash = digest.digest(seedBytes)

        return passHash!!
    }

    private fun createKey(keyGenerateSeed: String): SecretKey {
        this.generatePassHash(keyGenerateSeed)

        val secretKeySpec = SecretKeySpec(passHash, ALGORITHM)
        return secretKeySpec
    }

    fun getPassHash(keyGenerateSeed: String): ByteArray{
        return passHash?: this.generatePassHash(keyGenerateSeed)
    }

    fun encrypt(message: ByteArray, keyGenerateSeed: String): ByteArray{
        val encryptCipher = getEncryptionCipher(keyGenerateSeed)
        val encryptedMessage = encryptCipher.doFinal(message)
        iv = encryptCipher.iv

        val code = ByteArray(1)
        code[0] = encryptCipher.iv.size.toByte()
        return  code + encryptCipher.iv + encryptedMessage
    }

    fun decrypt(message: ByteArray, keyGenerateSeed: String): ByteArray {
        val ivSize = message[0].toInt()
        iv = ByteArray(ivSize)
        iv = message.copyOfRange(1, ivSize + 1)

        val encryptedMessage = message.copyOfRange(ivSize + 1, message.size)

        return getDecryptionCipher(iv, keyGenerateSeed).doFinal(encryptedMessage)
    }

}