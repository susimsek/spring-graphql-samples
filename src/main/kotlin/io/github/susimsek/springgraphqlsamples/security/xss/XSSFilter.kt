package io.github.susimsek.springgraphqlsamples.security.xss

import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.core.annotation.Order
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

@Order(SecurityProperties.DEFAULT_FILTER_ORDER-2)
class XSSFilter: CoWebFilter() {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val captureExchange = CaptureExchange(exchange)
        return chain.filter(captureExchange)
    }
}