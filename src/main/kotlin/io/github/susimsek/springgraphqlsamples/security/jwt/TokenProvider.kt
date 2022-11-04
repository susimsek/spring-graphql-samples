package io.github.susimsek.springgraphqlsamples.security.jwt

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

const val AUTHORITIES_KEY = "auth"

@Component
@EnableConfigurationProperties(TokenProperties::class)
class TokenProvider(
    tokenProperties: TokenProperties,
    private val jwtEncoder: JwtEncoder
) {
    private var tokenValidityInMilliseconds: Long = 0

    init {
        this.tokenValidityInMilliseconds = 1000 * tokenProperties.tokenValidityInSeconds
    }

    fun createToken(authentication: Authentication): String {
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
