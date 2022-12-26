package io.github.susimsek.springgraphqlsamples.security.jwt

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import java.time.Instant

const val AUTHORITIES_KEY = "auth"

class TokenProvider(
    private val tokenProperties: TokenProperties,
    private val jwtEncoder: JwtEncoder
) {

    fun createToken(authentication: Authentication): String {
        val tokenValidityInMilliseconds = 1000 * tokenProperties.tokenValidityInSeconds
        val authorities = authentication.authorities
            .map { it.authority }

        val now = Instant.now()

        val validity = now.plusMillis(tokenValidityInMilliseconds)

        val claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(validity)
            .subject(authentication.name)
            .claim(AUTHORITIES_KEY, authorities)
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }
}
