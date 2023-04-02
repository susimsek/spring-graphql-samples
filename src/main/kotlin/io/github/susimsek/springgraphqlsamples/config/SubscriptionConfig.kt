package io.github.susimsek.springgraphqlsamples.config

import io.github.susimsek.springgraphqlsamples.graphql.type.PostPayload
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.apache.kafka.common.security.authenticator.AbstractLogin
import org.apache.kafka.common.security.authenticator.DefaultLogin
import org.apache.kafka.common.security.authenticator.SaslClientCallbackHandler
import org.apache.kafka.common.security.plain.PlainLoginModule
import org.apache.kafka.common.security.scram.ScramLoginModule
import org.apache.kafka.common.security.scram.internals.ScramSaslClient
import org.slf4j.LoggerFactory
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.function.Consumer
import java.util.function.Supplier

@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(SubscriptionConfig.KafkaRuntimeHints::class)
class SubscriptionConfig {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun postFlow(): MutableSharedFlow<PostPayload> {
        return MutableSharedFlow(replay = 1)
    }

    @Bean
    fun postKafkaSink(): Sinks.Many<PostPayload> {
        return Sinks.many().multicast().onBackpressureBuffer()
    }

    @Bean
    fun postEventConsumer(
        postFlow: MutableSharedFlow<PostPayload>
    ): Consumer<PostPayload> =
        Consumer { msg ->
            logger.info("consumed payload: {}", msg)
            runBlocking { postFlow.emit(msg) }
        }

    @Bean
    fun postEventPublisher(postSink: Sinks.Many<PostPayload>): Supplier<Flux<PostPayload>> = Supplier {
        postSink.asFlux()
    }

    internal class KafkaRuntimeHints : RuntimeHintsRegistrar {
        private val values: Array<MemberCategory> = MemberCategory.values()

        @Suppress("SpreadOperator")
        override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
            setOf(
                PlainLoginModule::class.java,
                SaslClientCallbackHandler::class.java,
                DefaultLogin::class.java,
                AbstractLogin.DefaultLoginCallbackHandler::class.java,
                ScramLoginModule::class.java,
                ScramSaslClient::class.java
            ).forEach { hints.reflection().registerType(it, *values) }
            hints.reflection().registerType(
                TypeReference.of(
                    "org.apache.kafka.common.security.scram.internals.ScramSaslClient\$ScramSaslClientFactory"
                ),
                *values
            )
            hints.resources().registerResourceBundle("sun.security.util.Resources")
        }
    }
}
