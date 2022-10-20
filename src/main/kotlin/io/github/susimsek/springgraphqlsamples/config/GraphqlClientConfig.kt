package io.github.susimsek.springgraphqlsamples.config

import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.client.HttpGraphQlClient
import org.springframework.web.reactive.function.client.WebClient

@Configuration(proxyBeanMethods = false)
class GraphqlClientConfig {

    @Bean
    @LoadBalanced
    fun loadBalancedWebClientBuilder(): WebClient.Builder {
        return WebClient.builder()
    }

    @Bean
    fun httpGraphQlClientBuilder(
        loadBalancedWebClientBuilder: WebClient.Builder
    ): HttpGraphQlClient.Builder<*> {
        val webClient = loadBalancedWebClientBuilder.build()
        return HttpGraphQlClient.builder(webClient)
    }
}
