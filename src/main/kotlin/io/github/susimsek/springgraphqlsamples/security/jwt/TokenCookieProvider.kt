package io.github.susimsek.springgraphqlsamples.security.jwt

import io.github.susimsek.springgraphqlsamples.graphql.type.Token
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class TokenCookieProvider {
    fun createTokenCookie(token: Token): ResponseCookie {
        return ResponseCookie.from(TOKEN_COOKIE_NAME, token.token)
            .maxAge(token.expiresIn)
            .httpOnly(true)
            .path("/")
            .build()
    }
}