package io.github.susimsek.springgraphqlsamples.security.cipher

import io.github.susimsek.springgraphqlsamples.security.cipher.CryptoUtils.getAESKeyFromPassword
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

private const val ENCRYPT_ALGO = "AES/GCM/NoPadding"
private const val TAG_LENGTH_BIT = 128

private const val IV_LENGTH_BYTE = 12
private const val SALT_LENGTH_BYTE = 16
private val UTF_8 = StandardCharsets.UTF_8

@Suppress("TooGenericExceptionCaught")
@Component
@EnableConfigurationProperties(CipherProperties::class)
class SecurityCipher(
    private val cipherProperties: CipherProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private var cipher: Cipher = Cipher.getInstance(ENCRYPT_ALGO)

    fun encrypt(strToEncrypt: String): String? {
        try {
            val salt = CryptoUtils.getRandomNonce(SALT_LENGTH_BYTE)
            val iv = CryptoUtils.getRandomNonce(IV_LENGTH_BYTE)
            val keyBytes =  com.nimbusds.jose.util.Base64(cipherProperties.base64Secret).decode()
            val secretKey = getAESKeyFromPassword(String(keyBytes).toCharArray(), salt)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(TAG_LENGTH_BIT, iv))
            val cipherText = cipher.doFinal(strToEncrypt.toByteArray(UTF_8))
            val cipherTextWithIvSalt = ByteBuffer.allocate(iv.size + salt.size + cipherText.size)
                .put(iv)
                .put(salt)
                .put(cipherText)
                .array()
            return Base64.getEncoder().encodeToString(cipherTextWithIvSalt)
        } catch (e: Exception) {
            log.trace("Cipher encrypt error {}", e.message)
        }
        return null
    }

    fun decrypt(strToDecrypt: String): String? {
        try {
            val decode = Base64.getDecoder().decode(strToDecrypt.toByteArray(UTF_8))
            val bb = ByteBuffer.wrap(decode)

            val iv = ByteArray(IV_LENGTH_BYTE)
            bb.get(iv)

            val salt = ByteArray(SALT_LENGTH_BYTE)
            bb.get(salt)

            val cipherText = ByteArray(bb.remaining())
            bb.get(cipherText)

            val keyBytes =  com.nimbusds.jose.util.Base64(cipherProperties.base64Secret).decode()
            val secretKey = getAESKeyFromPassword(String(keyBytes).toCharArray(), salt)

            cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(TAG_LENGTH_BIT, iv))
            val plainText = cipher.doFinal(cipherText)
            return String(plainText, UTF_8)
        } catch (e: Exception) {
            log.trace("Cipher decrypt error {}", e.message)
        }
        return null
    }
}
