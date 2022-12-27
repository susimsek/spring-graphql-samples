package io.github.susimsek.springgraphqlsamples.security.jwt

import io.github.susimsek.springgraphqlsamples.graphql.type.Token
import org.springframework.http.ResponseCookie
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

    fun createToken(authentication: Authentication): Token {
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

        val  tokenValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
        return Token(
            token = tokenValue,
            expiresIn = tokenProperties.tokenValidityInSeconds
        )
    }

    fun createTokenCookie(token: Token): ResponseCookie {
        return ResponseCookie.from(TOKEN_COOKIE_NAME, token.token)
            .maxAge(token.expiresIn)
            .httpOnly(true)
            .path("/")
            .build()
    }

    fun deleteTokenCookie(): ResponseCookie {
        return ResponseCookie.from(TOKEN_COOKIE_NAME, "")
            .maxAge(0)
            .httpOnly(true)
            .path("/")
            .build()
    }
}
