package io.github.susimsek.springgraphqlsamples.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration(proxyBeanMethods = false)
class WebClientConfig {
    @Bean
    fun webClientBuilder(): WebClient.Builder {
        return WebClient.builder()
    }
}