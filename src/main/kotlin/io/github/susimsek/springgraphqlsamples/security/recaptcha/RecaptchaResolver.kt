package io.github.susimsek.springgraphqlsamples.security.recaptcha

import org.springframework.web.server.ServerWebExchange

fun interface RecaptchaResolver {
    fun resolve(exchange: ServerWebExchange): String?
}