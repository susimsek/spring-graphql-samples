package io.github.susimsek.springgraphqlsamples.security

import io.github.susimsek.springgraphqlsamples.security.jwt.TOKEN_COOKIE_NAME
import io.github.susimsek.springgraphqlsamples.security.jwt.TOKEN_PREFIX
import io.github.susimsek.springgraphqlsamples.security.jwt.WS_TOKEN_KEY_NAME
import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import org.springframework.graphql.server.WebSocketGraphQlInterceptor
import org.springframework.graphql.server.WebSocketGraphQlRequest
import org.springframework.graphql.server.WebSocketSessionInfo
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import reactor.core.publisher.Mono
import java.net.HttpCookie

class GraphQlWsAuthenticationInterceptor(
    private val reactiveAuthenticationManager: ReactiveAuthenticationManager
) : WebSocketGraphQlInterceptor {
    private companion object {

        private val AUTHENTICATION_SESSION_ATTRIBUTE_KEY =
            GraphQlWsAuthenticationInterceptor::class.qualifiedName + ".authentication"

        fun WebSocketSessionInfo.getAuthentication(): BearerTokenAuthenticationToken? =
            attributes[AUTHENTICATION_SESSION_ATTRIBUTE_KEY] as? BearerTokenAuthenticationToken

        fun WebSocketSessionInfo.setAuthentication(authentication: BearerTokenAuthenticationToken) {
            attributes[AUTHENTICATION_SESSION_ATTRIBUTE_KEY] = authentication
        }
    }

    override fun intercept(request: WebGraphQlRequest, chain: WebGraphQlInterceptor.Chain): Mono<WebGraphQlResponse> {

        if (request !is WebSocketGraphQlRequest) {
            return chain.next(request)
        }

        val securityContext = Mono.just(request)
            .mapNotNull { it.sessionInfo.getAuthentication() }
            .flatMap { reactiveAuthenticationManager.authenticate(it) }
            .map { SecurityContextImpl(it) }

        return chain.next(request)
            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(securityContext))
    }

    override fun handleConnectionInitialization(
        sessionInfo: WebSocketSessionInfo,
        connectionInitPayload: MutableMap<String, Any>,
    ): Mono<Any> {
        val jwtToken = resolveToken(sessionInfo, connectionInitPayload) ?: return Mono.empty()
        val token = BearerTokenAuthenticationToken(jwtToken)
        sessionInfo.setAuthentication(token)
        return Mono.empty()
    }

    private fun resolveToken(
        sessionInfo: WebSocketSessionInfo,
        connectionInitPayload: MutableMap<String, Any>): String? {
        return resolveTokenFromCookie(sessionInfo.headers)
            ?: resolveTokenFromPayload(connectionInitPayload)
    }

    private fun resolveTokenFromPayload(connectionInitPayload: MutableMap<String, Any>): String? {
       return (connectionInitPayload[WS_TOKEN_KEY_NAME] as? String)
            ?.takeIf { it.startsWith(TOKEN_PREFIX, ignoreCase = true) }
            ?.substring(TOKEN_PREFIX.length)
    }

    private fun resolveTokenFromCookie(headers: HttpHeaders): String? {
        val cookie = headers.getFirst(HttpHeaders.COOKIE) ?: return null
       val tokenCookie =  cookie.split(";")
            .flatMap { HttpCookie.parse(it)  }
            .find { it.name == TOKEN_COOKIE_NAME }
       return tokenCookie?.value
    }

}
