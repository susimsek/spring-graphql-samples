package io.github.susimsek.springgraphqlsamples.security.jwt

import io.github.susimsek.springgraphqlsamples.security.cipher.SecurityCipher
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class JWTFilter(
    private val tokenProvider: TokenProvider,
    private val securityCipher: SecurityCipher
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val jwt = resolveToken(exchange.request)
        if (!jwt.isNullOrBlank() && this.tokenProvider.validateToken(jwt)) {
            val authentication = this.tokenProvider.getAuthentication(jwt)
            return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
        }
        return chain.filter(exchange)
    }

    private fun resolveToken(request: ServerHttpRequest): String? {
        val bearerToken = request.headers.getFirst(AUTHORIZATION_HEADER)
        if (!bearerToken.isNullOrBlank() && bearerToken.startsWith("Bearer ")) {
            return securityCipher.decrypt(bearerToken.substring(7))
        }
        return null
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}
