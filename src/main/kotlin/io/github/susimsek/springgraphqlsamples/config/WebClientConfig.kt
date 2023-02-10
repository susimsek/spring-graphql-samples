package io.github.susimsek.springgraphqlsamples.config

import io.github.susimsek.springgraphqlsamples.exception.RateLimitingException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration(proxyBeanMethods = false)
class WebClientConfig {
    @Bean
    fun webClientBuilder(): WebClient.Builder {
        return WebClient.builder()
            .filter(errorHandler())
    }

    fun errorHandler(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofResponseProcessor { clientResponse ->
            if (clientResponse.statusCode().value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                clientResponse.bodyToMono(String::class.java)
                    .flatMap {
                        Mono.error(RateLimitingException())
                    }
            } else {
                Mono.just(
                    clientResponse
                )
            }
        }
    }
}
