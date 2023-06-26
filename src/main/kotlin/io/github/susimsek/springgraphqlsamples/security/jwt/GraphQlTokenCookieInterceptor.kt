package io.github.susimsek.springgraphqlsamples.security.jwt

import io.github.susimsek.springgraphqlsamples.config.Token
import io.github.susimsek.springgraphqlsamples.graphql.REFRESH_TOKEN_CONTEXT_NAME
import io.github.susimsek.springgraphqlsamples.graphql.TOKEN_CONTEXT_NAME
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
        val refreshToken = request.cookies.getFirst(tokenProperties.refreshTokenCookieName)?.value
        refreshToken?.let { token ->
            request.configureExecutionInput { _, builder ->
                builder.graphQLContext(
                    mapOf(REFRESH_TOKEN_CONTEXT_NAME to token)
                ).build()
            }
        }
        return chain.next(request).doOnNext { response ->
            val tokenPayload: TokenPayload? = response.executionInput.graphQLContext[TOKEN_CONTEXT_NAME]
            tokenPayload?.let { token ->
                val accessTokenCookie = when (token.accessToken.isBlank()) {
                    true -> tokenProvider.deleteAccessTokenCookie()
                    else -> tokenProvider.createAccessTokenCookie(
                        Token(token.accessToken, token.accessTokenExpiresIn)
                    )
                }
                val refreshTokenCookie = when (token.refreshToken.isBlank()) {
                    true -> tokenProvider.deleteRefreshTokenCookie()
                    else -> tokenProvider.createRefreshTokenCookie(
                        Token(token.refreshToken, token.refreshTokenExpiresIn)
                    )
                }
                response.responseHeaders.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                response.responseHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            }
        }
    }
}
