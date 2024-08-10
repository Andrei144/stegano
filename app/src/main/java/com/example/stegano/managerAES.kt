package com.example.stegano

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

open class CryptoManagerAES {

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }   // Parameters for the cryptographic cipher

    private val  keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private lateinit var iv: ByteArray

    private val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
        init(Cipher.ENCRYPT_MODE, getKey())
    }

    private fun getDecryptionCipher(iv: ByteArray): Cipher{
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    private fun getKey(): SecretKey{
        val existingKey = keyStore.getEntry("AESkey", null) as?
                KeyStore.SecretKeyEntry

        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    "AESkey",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    fun encrypt(message: ByteArray): ByteArray{
        val encryptedMessage = encryptCipher.doFinal(message)
        iv = encryptCipher.iv

        val code = ByteArray(1)
        code[0] = encryptCipher.iv.size.toByte()
        return  code + encryptCipher.iv + encryptedMessage
    }

    fun decrypt(message: ByteArray): ByteArray {
        val ivSize = message[0].toInt()
        iv = ByteArray(ivSize)
        iv = message.copyOfRange(1, ivSize + 1)

        val encryptedMessage = message.copyOfRange(ivSize + 1, message.size)

        return getDecryptionCipher(iv).doFinal(encryptedMessage)
    }

}