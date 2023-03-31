package io.github.susimsek.springgraphqlsamples.security.cipher

import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

object RSAKeyUtils {

    private const val alg: String = "RSA"

    private val keyFactory = KeyFactory.getInstance(alg)

    fun generatePublicKey(key: String): PublicKey {
        val encoded = Base64.getDecoder().decode(key)
        val keySpec = X509EncodedKeySpec(encoded)
        return keyFactory.generatePublic(keySpec)
    }

    fun generatePrivateKey(key: String): PrivateKey {
        val encoded = Base64.getDecoder().decode(key)
        val keySpec = PKCS8EncodedKeySpec(encoded)
        return keyFactory.generatePrivate(keySpec)
    }
}
