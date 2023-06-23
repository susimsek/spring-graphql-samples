package io.github.susimsek.springgraphqlsamples.security.jwt

import io.github.susimsek.springgraphqlsamples.config.Token
import io.github.susimsek.springgraphqlsamples.graphql.type.TokenPayload
import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import org.springframework.http.HttpHeaders
import reactor.core.publisher.Mono

class GraphQlTokenCookieInterceptor(
    private val tokenProvider: TokenProvider,
    private val tokenProperties: Token
) : WebGraphQlInterceptor {

    override fun intercept(
        request: WebGraphQlRequest,
        chain: WebGraphQlInterceptor.Chain
    ): Mono<WebGraphQlResponse> {
        val refreshToken = request.cookies.getFirst(tokenProperties.refreshTokenCookieName)?.value ?: ""
        request.configureExecutionInput { _, builder ->
            builder.graphQLContext(
                mapOf("refreshToken" to refreshToken)
            ).build()
        }
        return chain.next(request).doOnNext { response ->
            val token: TokenPayload? = response.executionInput.graphQLContext["token"]
            if (token != null) {
                val accessTokenCookie = when (token.accessToken.isBlank()) {
                    true -> tokenProvider.deleteAccessTokenCookie()
                    else -> tokenProvider.createAccessTokenCookie(token)
                }
                val refreshTokenCookie = when (token.refreshToken.isBlank()) {
                    true -> tokenProvider.deleteRefreshTokenCookie()
                    else -> tokenProvider.createRefreshTokenCookie(token)
                }
                response.responseHeaders.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                response.responseHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            }
        }
    }
}
