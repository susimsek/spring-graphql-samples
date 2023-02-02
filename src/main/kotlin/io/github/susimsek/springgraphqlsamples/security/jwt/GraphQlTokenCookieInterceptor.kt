package io.github.susimsek.springgraphqlsamples.security.jwt

import io.github.susimsek.springgraphqlsamples.graphql.type.Token
import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import org.springframework.http.HttpHeaders
import reactor.core.publisher.Mono

class GraphQlTokenCookieInterceptor(
    private val tokenProvider: TokenProvider
) : WebGraphQlInterceptor {

    override fun intercept(
        request: WebGraphQlRequest,
                           chain: WebGraphQlInterceptor.Chain
    ): Mono<WebGraphQlResponse> {
        return chain.next(request).doOnNext { response ->
            val token = response.executionInput.graphQLContext.get<Token>("token")
            if (token != null) {
                val cookie = when (token.token.isBlank()) {
                    true -> tokenProvider.deleteTokenCookie()
                    else -> tokenProvider.createTokenCookie(token)
                }
                response.responseHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString())
            }
        }
    }
}
