package io.github.susimsek.springgraphqlsamples.security.cipher

import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {
    fun getRandomNonce(numBytes: Int): ByteArray {
        val nonce = ByteArray(numBytes)
        SecureRandom().nextBytes(nonce)
        return nonce
    }

    // AES secret key
    @Throws(NoSuchAlgorithmException::class)
    fun getAESKey(keySize: Int): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(keySize, SecureRandom.getInstanceStrong())
        return keyGen.generateKey()
    }

    // Password derived AES 256 bits secret key
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getAESKeyFromPassword(password: CharArray?, salt: ByteArray?): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        // iterationCount = 65536
        // keyLength = 256
        val spec: KeySpec = PBEKeySpec(password, salt, 65536, 256)
        return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }
}
