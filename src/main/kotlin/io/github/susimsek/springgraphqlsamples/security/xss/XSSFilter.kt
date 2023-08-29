package io.github.susimsek.springgraphqlsamples.security.xss

import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

class XSSFilter: CoWebFilter() {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        val captureExchange = CaptureExchange(exchange)
        return chain.filter(captureExchange)
    }
}