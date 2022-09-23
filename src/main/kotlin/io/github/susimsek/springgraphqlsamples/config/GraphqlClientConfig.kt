package io.github.susimsek.springgraphqlsamples.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.client.HttpGraphQlClient
import org.springframework.web.reactive.function.client.WebClient

@Configuration(proxyBeanMethods = false)
class GraphqlClientConfig {

    @Bean
    fun webClientBuilder(): WebClient.Builder {
        return WebClient.builder()
    }

    @Bean
    fun httpGraphQlClientBuilder(
        webClientBuilder: WebClient.Builder
    ): HttpGraphQlClient.Builder<*> {
        val webClient = webClientBuilder.build()
        return HttpGraphQlClient.builder(webClient)
    }
}
