package io.github.susimsek.springgraphqlsamples.security.recaptcha

import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import reactor.core.publisher.Mono

class GraphQlRecaptchaHeaderInterceptor : WebGraphQlInterceptor {

    override fun intercept(request: WebGraphQlRequest, chain: WebGraphQlInterceptor.Chain): Mono<WebGraphQlResponse> {
        val recaptcha = request.headers.getFirst("recaptcha") ?: ""
        request.configureExecutionInput { _, builder ->
            builder.graphQLContext(
                mapOf("recaptcha" to recaptcha)
            ).build()
        }
        return chain.next(request)
    }
}
