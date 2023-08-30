package io.github.susimsek.springgraphqlsamples.security.recaptcha

import io.github.susimsek.springgraphqlsamples.exception.InvalidCaptchaException
import io.github.susimsek.springgraphqlsamples.exception.RECAPTCHA_INVALID_MSG_CODE
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


class RecaptchaFilter(
    private val recaptchaService: RecaptchaService,
    private val recaptchaProperties: RecaptchaProperties
): CoWebFilter() {

    private val recaptchaResolver = DefaultRecaptchaResolver();

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        if (recaptchaProperties.enabled) {
            val recaptcha = recaptchaResolver.resolve(exchange)
            val success = recaptchaService.validateToken(recaptcha)
            if (!success) {
                return exchange.response.writeWith(Mono.error(InvalidCaptchaException(RECAPTCHA_INVALID_MSG_CODE)))
                    .cast(Unit.javaClass).awaitSingleOrNull() ?: Unit
            }
        }
        return chain.filter(exchange)
    }
}