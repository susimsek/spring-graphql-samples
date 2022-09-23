package io.github.susimsek.springgraphqlsamples.config

import graphql.schema.GraphQLScalarType
import io.github.susimsek.springgraphqlsamples.graphql.scalar.GraphQlDateTimeProperties
import io.github.susimsek.springgraphqlsamples.graphql.scalar.ScalarUtil
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GraphQlDateTimeProperties::class)
class GraphqlDateTimeConfig {

    @Bean
    fun graphQlOffsetDateTimeScalar(configurationProperties: GraphQlDateTimeProperties): GraphQLScalarType {
        return ScalarUtil.offsetDateTimeScalar(
            configurationProperties.offsetDateTime.scalarName,
            configurationProperties.offsetDateTime.format
        )
    }

    @Bean
    fun graphQlLocalDateScalar(configurationProperties: GraphQlDateTimeProperties): GraphQLScalarType {
        return ScalarUtil.localDateScalar(
            configurationProperties.localDate.scalarName,
            configurationProperties.localDate.format
        )
    }

    @Bean
    fun graphQlLocalDateTimeScalar(configurationProperties: GraphQlDateTimeProperties): GraphQLScalarType {
        return ScalarUtil.localDateTimeScalar(
            configurationProperties.localDateTime.scalarName,
            configurationProperties.localDateTime.format
        )
    }

    @Bean
    fun graphqlDateTimeConfigurer(
        graphQlOffsetDateTimeScalar: GraphQLScalarType,
        graphQlLocalDateTimeScalar: GraphQLScalarType,
        graphQlLocalDateScalar: GraphQLScalarType
    ): RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer { builder ->
            builder.scalar(graphQlOffsetDateTimeScalar)
            builder.scalar(graphQlLocalDateTimeScalar)
        }
    }
}
