package io.github.susimsek.springgraphqlsamples.config

import io.github.susimsek.springgraphqlsamples.service.chatgpt.ChatGptClient
import io.github.susimsek.springgraphqlsamples.service.chatgpt.ChatGptProperties
import io.github.susimsek.springgraphqlsamples.service.chatgpt.ChatGptService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ChatGptProperties::class)
class ChatGptConfig {

    @Bean
    fun chatGptClient(
        webClientBuilder: WebClient.Builder,
        chatGptProperties: ChatGptProperties
    ): ChatGptClient {
        val webClient = webClientBuilder.baseUrl(chatGptProperties.baseUrl)
            .build()
        val httpServiceProxyFactory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient))
            .build()
        return httpServiceProxyFactory.createClient(ChatGptClient::class.java)
    }

    @Bean
    fun chatGptService(
        chatGptClient: ChatGptClient,
        chatGptProperties: ChatGptProperties
    ): ChatGptService {
        return ChatGptService(chatGptClient, chatGptProperties)
    }
}
