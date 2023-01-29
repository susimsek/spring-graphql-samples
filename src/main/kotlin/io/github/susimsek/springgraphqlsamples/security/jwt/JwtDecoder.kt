package io.github.susimsek.springgraphqlsamples.security.jwt

import io.github.susimsek.springgraphqlsamples.security.cipher.SecurityCipher
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import reactor.core.publisher.Mono
import java.lang.IllegalStateException
import java.security.interfaces.RSAPublicKey

class JwtDecoder(
    publicKey: RSAPublicKey,
    private val securityCipher: SecurityCipher
) : ReactiveJwtDecoder {

    private var nimbusReactiveJwtDecoder = NimbusReactiveJwtDecoder.withPublicKey(publicKey)
        .build()

    override fun decode(token: String): Mono<Jwt> {
        val jwt = resolveToken(token)
        return nimbusReactiveJwtDecoder.decode(jwt)
    }

    private fun resolveToken(token: String): String? {
        if (token.isNotBlank()) {
            try {
                return securityCipher.decrypt(token)
            } catch (e: IllegalStateException) {
                throw BadJwtException("Invalid Jwt")
            }
        }
        return null
    }
}
