package io.github.susimsek.springgraphqlsamples.security.jwt

import org.springframework.http.HttpCookie
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.server.resource.BearerTokenErrors
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter
import org.springframework.util.CollectionUtils
import org.springframework.util.MultiValueMap
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class TokenAuthenticationConverter : ServerBearerTokenAuthenticationConverter() {

    private var tokenCookieName = TOKEN_COOKIE_NAME

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        val token = token(exchange.request)
        if (token.isNullOrBlank()) {
            return super.convert(exchange)
        }
        return Mono.just(BearerTokenAuthenticationToken(token))
    }

    private fun token(request: ServerHttpRequest): String? {
        return resolveAccessTokenFromCookie(request.cookies)
    }

    private fun resolveAccessTokenFromCookie(cookiesMap: MultiValueMap<String, HttpCookie>): String? {
        var token: String? = null
        if (!CollectionUtils.isEmpty(cookiesMap)) {
            val cookies = cookiesMap[tokenCookieName] ?: return null
             if (cookies.size == 1) {
                token = cookies.first().value
            } else {
                val error = BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request")
                throw OAuth2AuthenticationException(error)
            }
        }
        return token
    }

    fun setTokenCookieName(tokenCookieName: String) {
        this.tokenCookieName = tokenCookieName
    }
}
