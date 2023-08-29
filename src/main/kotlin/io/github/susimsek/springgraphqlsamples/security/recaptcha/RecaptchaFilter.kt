package io.github.susimsek.springgraphqlsamples.security.recaptcha

import io.github.susimsek.springgraphqlsamples.exception.InvalidCaptchaException
import io.github.susimsek.springgraphqlsamples.exception.RECAPTCHA_INVALID_MSG_CODE
import io.github.susimsek.springgraphqlsamples.graphql.RECAPTCHA_HEADER_NAME
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.core.annotation.Order
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono






@Order(SecurityProperties.DEFAULT_FILTER_ORDER-1)
class RecaptchaFilter(
    private val recaptchaService: RecaptchaService,
): CoWebFilter() {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val recaptcha = exchange.request.headers.getFirst(RECAPTCHA_HEADER_NAME)
        val success = recaptchaService.validateToken(recaptcha)
        if (!success) {
            return exchange.response.writeWith(Mono.error(InvalidCaptchaException(RECAPTCHA_INVALID_MSG_CODE)))
                .cast(Unit.javaClass).awaitSingleOrNull() ?: Unit
        }
        return chain.filter(exchange)
    }
}