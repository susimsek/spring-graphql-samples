package io.github.susimsek.springgraphqlsamples.graphql

import io.github.susimsek.springgraphqlsamples.config.GraphqlConfig
import io.github.susimsek.springgraphqlsamples.config.ValidationConfig
import io.github.susimsek.springgraphqlsamples.exception.handler.GraphqlExceptionHandler
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.AliasFor
import org.springframework.graphql.execution.ReactiveSecurityDataFetcherExceptionResolver
import kotlin.reflect.KClass

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@GraphQlTest
@Import(
    ValidationConfig::class,
    GraphqlConfig::class,
    MessageSourceAutoConfiguration::class,
    ReactiveSecurityDataFetcherExceptionResolver::class,
    GraphqlExceptionHandler::class
)
annotation class GraphQlUnitTest(
    @get:AliasFor(annotation = GraphQlTest::class, attribute = "controllers")
    val value: Array<KClass<*>> = []
)
