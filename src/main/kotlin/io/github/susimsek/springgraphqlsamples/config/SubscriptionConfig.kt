package io.github.susimsek.springgraphqlsamples.config

import io.github.susimsek.springgraphqlsamples.graphql.type.PostPayload
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.function.Consumer
import java.util.function.Supplier


@Configuration(proxyBeanMethods = false)
class SubscriptionConfig {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun postFlow(): MutableSharedFlow<PostPayload> {
        return MutableSharedFlow(replay = 1)
    }

    @Bean
    fun postSink(): Sinks.Many<PostPayload> {
        return  Sinks.many().multicast().onBackpressureBuffer()
    }

    @Bean
    fun postEventConsumer(
        postFlow: MutableSharedFlow<PostPayload>)
            : Consumer<Flux<PostPayload>> = Consumer { stream ->
        stream.concatMap { msg ->
            logger.info("consumed payload: {}", msg)
            mono { postFlow.emit(msg) }
        }.onErrorContinue { e, _ ->
            logger.error(e.message, e)
        }.subscribe()
    }

    @Bean
    fun postEventPublisher(postSink: Sinks.Many<PostPayload>): Supplier<Flux<PostPayload>> = Supplier {
        postSink.asFlux()
    }
}