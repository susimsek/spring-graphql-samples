package io.github.susimsek.springgraphqlsamples.security.xss

import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator

class CaptureExchange(delegate: ServerWebExchange) : ServerWebExchangeDecorator(delegate) {
    private val xssRequestWrapper: XSSRequestWrapper

    init {
        xssRequestWrapper = XSSRequestWrapper(delegate.request)
    }

    override fun getRequest(): XSSRequestWrapper {
        return xssRequestWrapper
    }
}
