package io.github.susimsek.springgraphqlsamples.config

import io.github.susimsek.springgraphqlsamples.exception.RateLimitingException
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Configuration(proxyBeanMethods = false)
class WebClientConfig {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun webClientBuilder(): WebClient.Builder {
        return WebClient.builder()
            .filter(errorHandler())
    }

    private fun errorHandler(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofResponseProcessor { clientResponse ->
            when (hasError(clientResponse)) {
                true -> handleError(clientResponse)
                else -> Mono.just(clientResponse)
            }
        }
    }

    fun handleError(clientResponse: ClientResponse): Mono<ClientResponse> {
        return when (clientResponse.statusCode().value()) {
            HttpStatus.TOO_MANY_REQUESTS.value() -> {
                clientResponse.bodyToMono<String>().map { errorBody ->
                    log.error(
                        "Error Response code is : {} and the message is {}",
                        clientResponse.statusCode(),
                        errorBody
                    )
                    RateLimitingException(errorBody)
                }
            }
            else -> clientResponse.createException()
        }.flatMap { error -> Mono.error(error) }
    }

    fun hasError(clientResponse: ClientResponse): Boolean {
        return clientResponse.statusCode().is4xxClientError || clientResponse.statusCode().is5xxServerError
    }
}
