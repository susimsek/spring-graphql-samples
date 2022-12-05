package io.github.susimsek.springgraphqlsamples.security

import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import org.springframework.graphql.server.WebSocketGraphQlInterceptor
import org.springframework.graphql.server.WebSocketGraphQlRequest
import org.springframework.graphql.server.WebSocketSessionInfo
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import reactor.core.publisher.Mono

class WebSocketAuthenticationInterceptor(
    private val reactiveAuthenticationManager: ReactiveAuthenticationManager
) : WebSocketGraphQlInterceptor {
    private companion object {
        const val TOKEN_KEY_NAME = "authorization"
        const val TOKEN_PREFIX = "Bearer "

        private val AUTHENTICATION_SESSION_ATTRIBUTE_KEY =
            WebSocketAuthenticationInterceptor::class.qualifiedName + ".authentication"

        fun WebSocketSessionInfo.getAuthentication(): BearerTokenAuthenticationToken? =
            attributes[AUTHENTICATION_SESSION_ATTRIBUTE_KEY] as? BearerTokenAuthenticationToken

        fun WebSocketSessionInfo.setAuthentication(authentication: BearerTokenAuthenticationToken) {
            attributes[AUTHENTICATION_SESSION_ATTRIBUTE_KEY] = authentication
        }
    }

    override fun intercept(request: WebGraphQlRequest, chain: WebGraphQlInterceptor.Chain): Mono<WebGraphQlResponse> {
        val token = (request as? WebSocketGraphQlRequest)?.sessionInfo?.getAuthentication()
            ?: return chain.next(request)

        val securityContext =  reactiveAuthenticationManager.authenticate(token)
            .map { SecurityContextImpl(it) }

        return chain.next(request)
            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(securityContext))
    }

    override fun handleConnectionInitialization(
        sessionInfo: WebSocketSessionInfo,
        connectionInitPayload: MutableMap<String, Any>,
    ): Mono<Any> {
        val jwtToken = resolveToken(connectionInitPayload) ?: return Mono.empty()
        val token = BearerTokenAuthenticationToken(jwtToken)
        sessionInfo.setAuthentication(token)
        return Mono.empty()
    }

    private fun resolveToken(connectionInitPayload: MutableMap<String, Any>): String? {
       return (connectionInitPayload[TOKEN_KEY_NAME] as? String)
            ?.takeIf { it.startsWith(TOKEN_PREFIX, ignoreCase = true) }
            ?.substring(TOKEN_PREFIX.length)
    }
}
