package io.github.susimsek.springgraphqlsamples.security.jwt

import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import io.github.susimsek.springgraphqlsamples.security.cipher.SecurityCipher
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import reactor.core.publisher.Mono
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import javax.crypto.spec.SecretKeySpec

class JwtDecoder(
    publicKey: RSAPublicKey,
    private val securityCipher: SecurityCipher
) : ReactiveJwtDecoder {

    private var nimbusReactiveJwtDecoder: NimbusReactiveJwtDecoder = NimbusReactiveJwtDecoder.withPublicKey(publicKey)
        .build()

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
