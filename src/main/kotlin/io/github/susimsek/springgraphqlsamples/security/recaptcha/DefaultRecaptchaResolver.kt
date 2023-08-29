package io.github.susimsek.springgraphqlsamples.security.recaptcha

import io.github.susimsek.springgraphqlsamples.exception.InvalidCaptchaException
import io.github.susimsek.springgraphqlsamples.exception.RECAPTCHA_INVALID_MSG_CODE
import io.github.susimsek.springgraphqlsamples.graphql.RECAPTCHA_HEADER_NAME
import org.springframework.util.StringUtils
import org.springframework.web.server.ServerWebExchange
import java.util.regex.Pattern

class DefaultRecaptchaResolver : RecaptchaResolver {

    private val recaptchaPattern = Pattern.compile("[A-Za-z0-9_-]+")

    override fun resolve(exchange: ServerWebExchange): String? {
       return resolveFromRecaptchaHeader(exchange);
    }

    private fun resolveFromRecaptchaHeader(exchange: ServerWebExchange): String? {
        val recaptcha: String = exchange.request.headers.getFirst(RECAPTCHA_HEADER_NAME)
        return if (!StringUtils.hasText(recaptcha)) {
            null
        } else {
            val matcher = recaptchaPattern.matcher(recaptcha)
            if (!matcher.matches()) {
                throw InvalidCaptchaException(RECAPTCHA_INVALID_MSG_CODE)
            }
            return recaptcha;
        }
    }
}