package io.github.susimsek.springgraphqlsamples.security.recaptcha

import io.github.susimsek.springgraphqlsamples.graphql.RECAPTCHA_CONTEXT_NAME
import io.github.susimsek.springgraphqlsamples.graphql.RECAPTCHA_HEADER_NAME
import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import reactor.core.publisher.Mono

class GraphQlRecaptchaHeaderInterceptor : WebGraphQlInterceptor {

    override fun intercept(request: WebGraphQlRequest, chain: WebGraphQlInterceptor.Chain): Mono<WebGraphQlResponse> {
        val header = request.headers.getFirst(RECAPTCHA_HEADER_NAME)
        header?.let { recaptcha ->
            request.configureExecutionInput { _, builder ->
                builder.graphQLContext(
                    mapOf(RECAPTCHA_CONTEXT_NAME to recaptcha)
                ).build()
            }
        }
        return chain.next(request)
    }
}
