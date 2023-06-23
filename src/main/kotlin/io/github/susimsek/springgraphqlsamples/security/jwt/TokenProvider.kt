package io.github.susimsek.springgraphqlsamples.security.jwt

import io.github.susimsek.springgraphqlsamples.graphql.type.TokenPayload
import org.springframework.http.ResponseCookie
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.util.UUID

const val AUTHORITIES_KEY = "auth"

class TokenProvider(
    private val tokenProperties: io.github.susimsek.springgraphqlsamples.config.Token,
    private val jwtEncoder: JwtEncoder,
) {
    private val tokenValidityInMilliseconds = 1000 * tokenProperties.accessTokenValidityInSeconds

    fun createAccessToken(userDetails: UserDetails): Token {
        val authorities = userDetails.authorities
            .map { it.authority }

        val now = Instant.now()

        val validity = now.plusMillis(tokenValidityInMilliseconds)

        val claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(validity)
            .subject(userDetails.username)
            .claim(AUTHORITIES_KEY, authorities)
            .build()

        val tokenValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
        return Token(
            token = tokenValue,
            expiresIn = tokenProperties.accessTokenValidityInSeconds
        )
    }

    fun calculateRemainingExpiresIn(expiryDate: OffsetDateTime): Long {
        val now = OffsetDateTime.now()
        return Duration.between(now, expiryDate).seconds
    }

    fun createRefreshToken(): Token {
        val tokenValue = UUID.randomUUID().toString()
        return Token(
            token = tokenValue,
            expiresIn = tokenProperties.refreshTokenValidityInSeconds
        )
    }

    fun createAccessTokenCookie(token: TokenPayload): ResponseCookie {
        return createCookie(
            tokenProperties.accessTokenCookieName,
            token.accessToken,
            token.accessTokenExpiresIn
        )
    }

    fun createRefreshTokenCookie(token: TokenPayload): ResponseCookie {
        return createCookie(
            tokenProperties.refreshTokenCookieName,
            token.refreshToken,
            token.refreshTokenExpiresIn
        )
    }

    fun deleteAccessTokenCookie(): ResponseCookie {
        return deleteCookie(tokenProperties.accessTokenCookieName)
    }

    fun deleteRefreshTokenCookie(): ResponseCookie {
        return deleteCookie(tokenProperties.refreshTokenCookieName)
    }

    private fun createCookie(
        name: String,
        value: String,
        maxAge: Long
    ): ResponseCookie {
        return ResponseCookie.from(name, value)
            .maxAge(maxAge)
            .httpOnly(true)
            .path("/")
            .domain(tokenProperties.cookieDomain)
            .build()
    }

    private fun deleteCookie(name: String): ResponseCookie {
        return ResponseCookie.from(name, "")
            .maxAge(0)
            .httpOnly(true)
            .path("/")
            .domain(tokenProperties.cookieDomain)
            .build()
    }
}
