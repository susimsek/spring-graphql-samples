package io.github.susimsek.springgraphqlsamples.security.jwt

import io.github.susimsek.springgraphqlsamples.security.cipher.SecurityCipher
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import reactor.core.publisher.Mono
import javax.crypto.spec.SecretKeySpec

class JwtDecoder(
    base64Secret: String,
    private val securityCipher: SecurityCipher): ReactiveJwtDecoder {

    private var nimbusReactiveJwtDecoder: NimbusReactiveJwtDecoder

    init {
        val keyBytes = com.nimbusds.jose.util.Base64(base64Secret).decode()
        val secretKey = SecretKeySpec(keyBytes, "HmacSHA512")
        this.nimbusReactiveJwtDecoder = NimbusReactiveJwtDecoder.withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS512)
            .build()
    }


    override fun decode(token: String): Mono<Jwt> {
        val jwt = resolveToken(token)
        return nimbusReactiveJwtDecoder.decode(jwt)
    }

    private fun resolveToken(token: String): String? {
        if (token.isNotBlank()) {
           return securityCipher.decrypt(token) ?: throw BadJwtException("Invalid Jwt")
        }
        return null
    }
}