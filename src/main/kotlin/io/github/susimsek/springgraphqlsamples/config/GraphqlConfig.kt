package io.github.susimsek.springgraphqlsamples.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import graphql.scalars.ExtendedScalars
import graphql.schema.GraphQLScalarType
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.validation.rules.OnValidationErrorStrategy
import graphql.validation.rules.ValidationRules
import graphql.validation.schemawiring.ValidationSchemaWiring
import io.github.susimsek.springgraphqlsamples.graphql.directive.UppercaseDirective
import io.github.susimsek.springgraphqlsamples.graphql.scalar.GraphQlDateTimeProperties
import io.github.susimsek.springgraphqlsamples.graphql.scalar.ScalarUtil
import io.github.susimsek.springgraphqlsamples.graphql.validation.EmailRule
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.core.io.ClassPathResource
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GraphQlDateTimeProperties::class)
class GraphqlConfig {

    @Bean
    fun jsonCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { builder: Jackson2ObjectMapperBuilder ->
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            builder.featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            builder.modules(JavaTimeModule())
        }
    }

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

    fun graphQLObjectScalar(): GraphQLScalarType {
        return ExtendedScalars.Object
    }

    fun graphQLBigDecimalScalar(): GraphQLScalarType {
        return ExtendedScalars.GraphQLBigDecimal
    }

    fun urlScalar(): GraphQLScalarType {
        return ExtendedScalars.Url
    }

    fun graphQLPositiveIntScalar(): GraphQLScalarType {
        return ExtendedScalars.PositiveInt
    }

    fun graphQLUuidScalar(): GraphQLScalarType {
        return ExtendedScalars.UUID
    }

    fun localeScalar(): GraphQLScalarType {
        return ExtendedScalars.Locale
    }

    fun validationSchemaWiring(): SchemaDirectiveWiring {
        val validationRules = ValidationRules.newValidationRules()
            .onValidationErrorStrategy(OnValidationErrorStrategy.RETURN_NULL)
            .addRule(EmailRule())
            .build()
        return ValidationSchemaWiring(validationRules)
    }

    @Bean
    @ImportRuntimeHints(GraphqlRuntimeHintsRegistrar::class)
    fun graphqlDateTimeConfigurer(
        graphQlOffsetDateTimeScalar: GraphQLScalarType,
        graphQlLocalDateTimeScalar: GraphQLScalarType,
        graphQlLocalDateScalar: GraphQLScalarType
    ): RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer { builder ->
            builder.scalar(graphQlOffsetDateTimeScalar)
            builder.scalar(graphQlLocalDateTimeScalar)
            builder.scalar(graphQlLocalDateScalar)
            builder.scalar(graphQLBigDecimalScalar())
            builder.scalar(graphQLBigDecimalScalar())
            builder.scalar(graphQLPositiveIntScalar())
            builder.scalar(graphQLUuidScalar())
            builder.scalar(graphQLObjectScalar())
            builder.scalar(urlScalar())
            builder.scalar(localeScalar())
            builder.directiveWiring(validationSchemaWiring())
            builder.directive("uppercase", UppercaseDirective())
        }
    }

    companion object {
        val SCHEMA_RESOURCE = ClassPathResource("/graphql/schema.graphqls")
    }
}
