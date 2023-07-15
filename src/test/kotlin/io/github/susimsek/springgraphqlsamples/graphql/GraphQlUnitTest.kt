package io.github.susimsek.springgraphqlsamples.graphql

import io.github.susimsek.springgraphqlsamples.config.GraphqlConfig
import io.github.susimsek.springgraphqlsamples.config.TestSecurityConfig
import io.github.susimsek.springgraphqlsamples.config.WebFluxConfig
import io.github.susimsek.springgraphqlsamples.exception.handler.GraphqlExceptionHandler
import io.github.susimsek.springgraphqlsamples.exception.handler.ReactiveSecurityExceptionResolver
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.AliasFor
import kotlin.reflect.KClass

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@GraphQlTest(
    excludeAutoConfiguration = [
        ReactiveUserDetailsServiceAutoConfiguration::class,
        ReactiveSecurityAutoConfiguration::class
    ]
)
@Import(
    GraphqlConfig::class,
    WebFluxConfig::class,
    MessageSourceAutoConfiguration::class,
    ReactiveSecurityExceptionResolver::class,
    GraphqlExceptionHandler::class,
    TestSecurityConfig::class
)
annotation class GraphQlUnitTest(
    @get:AliasFor(annotation = GraphQlTest::class, attribute = "controllers")
    val value: Array<KClass<*>> = []
)
