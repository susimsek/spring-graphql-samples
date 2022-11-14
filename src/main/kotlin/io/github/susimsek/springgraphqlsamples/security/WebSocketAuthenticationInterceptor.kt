package io.github.susimsek.springgraphqlsamples.security

import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import org.springframework.graphql.server.WebSocketGraphQlInterceptor
import org.springframework.graphql.server.WebSocketGraphQlRequest
import org.springframework.graphql.server.WebSocketSessionInfo
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class WebSocketAuthenticationInterceptor(
    private val jwtReactiveAuthenticationManager: JwtReactiveAuthenticationManager
) : WebSocketGraphQlInterceptor {
    private companion object {
        const val TOKEN_KEY_NAME = "authToken"
        const val TOKEN_PREFIX = "Bearer "

        private val AUTHENTICATION_SESSION_ATTRIBUTE_KEY =
            WebSocketAuthenticationInterceptor::class.qualifiedName + ".authentication"

        fun WebSocketSessionInfo.getAuthentication(): JwtAuthenticationToken? =
            attributes[AUTHENTICATION_SESSION_ATTRIBUTE_KEY] as? JwtAuthenticationToken

        fun WebSocketSessionInfo.setAuthentication(authentication: Authentication) {
            attributes[AUTHENTICATION_SESSION_ATTRIBUTE_KEY] = authentication
        }
    }

    override fun intercept(request: WebGraphQlRequest, chain: WebGraphQlInterceptor.Chain): Mono<WebGraphQlResponse> {
        val authentication = (request as? WebSocketGraphQlRequest)?.sessionInfo?.getAuthentication()
            ?: return chain.next(request)

        return chain.next(request)
            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
    }

    override fun handleConnectionInitialization(
        sessionInfo: WebSocketSessionInfo,
        connectionInitPayload: MutableMap<String, Any>,
    ): Mono<Any> {
        val jwtToken = resolveToken(connectionInitPayload) ?: return Mono.empty()
        val token = BearerTokenAuthenticationToken(jwtToken)

        return jwtReactiveAuthenticationManager.authenticate(token)
            .doOnNext { sessionInfo.setAuthentication(it) }
            .flatMap { Mono.empty() }
    }

    private fun resolveToken(connectionInitPayload: MutableMap<String, Any>): String? {
       return (connectionInitPayload[TOKEN_KEY_NAME] as? String)
            ?.takeIf { it.startsWith(TOKEN_PREFIX, ignoreCase = true) }
            ?.substring(TOKEN_PREFIX.length)
    }
}
