package io.github.susimsek.springgraphqlsamples.security.jwt

import io.github.susimsek.springgraphqlsamples.util.CookieUtil
import org.springframework.http.ResponseCookie
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*

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

    fun createAccessTokenCookie(token: Token): ResponseCookie {
        return CookieUtil.createHttpOnlyCookie(
            tokenProperties.accessTokenCookieName,
            token.token,
            token.expiresIn,
            tokenProperties.cookieDomain
        )
    }

    fun createRefreshTokenCookie(token: Token): ResponseCookie {
        return CookieUtil.createHttpOnlyCookie(
            tokenProperties.refreshTokenCookieName,
            token.token,
            token.expiresIn,
            tokenProperties.cookieDomain
        )
    }

    fun deleteAccessTokenCookie(): ResponseCookie {
        return CookieUtil.deleteHttpOnlyCookie(
            tokenProperties.accessTokenCookieName,
            tokenProperties.cookieDomain
        )
    }

    fun deleteRefreshTokenCookie(): ResponseCookie {
        return CookieUtil.deleteHttpOnlyCookie(
            tokenProperties.refreshTokenCookieName,
            tokenProperties.cookieDomain
        )
    }
}
