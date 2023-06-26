package io.github.susimsek.springgraphqlsamples.rest

import io.github.susimsek.springgraphqlsamples.config.GraphqlConfig
import io.github.susimsek.springgraphqlsamples.config.WebFluxConfig
import io.github.susimsek.springgraphqlsamples.exception.handler.ReactiveSecurityExceptionResolver
import io.github.susimsek.springgraphqlsamples.exception.handler.RestExceptionHandler
import io.github.susimsek.springgraphqlsamples.graphql.config.TestSecurityConfig
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.AliasFor
import kotlin.reflect.KClass

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@WebFluxTest(
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
    RestExceptionHandler::class,
    TestSecurityConfig::class
)
annotation class WebFluxUnitTest(
    @get:AliasFor(annotation = WebFluxTest::class, attribute = "controllers")
    val value: Array<KClass<*>> = []
)
