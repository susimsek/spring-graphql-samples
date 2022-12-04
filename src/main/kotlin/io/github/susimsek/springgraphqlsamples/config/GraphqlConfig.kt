package io.github.susimsek.springgraphqlsamples.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import graphql.scalars.ExtendedScalars
import graphql.schema.DataFetchingEnvironmentImpl
import graphql.schema.GraphQLAppliedDirective
import graphql.schema.GraphQLScalarType
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.validation.rules.OnValidationErrorStrategy
import graphql.validation.rules.ValidationRules
import graphql.validation.schemawiring.ValidationSchemaWiring
import io.github.susimsek.springgraphqlsamples.graphql.directive.CapitalizeDirective
import io.github.susimsek.springgraphqlsamples.graphql.directive.LowercaseDirective
import io.github.susimsek.springgraphqlsamples.graphql.directive.SchemaDirective
import io.github.susimsek.springgraphqlsamples.graphql.directive.TrimDirective
import io.github.susimsek.springgraphqlsamples.graphql.directive.UppercaseDirective
import io.github.susimsek.springgraphqlsamples.graphql.validation.EmailRule
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import org.springframework.graphql.server.support.GraphQlWebSocketMessage
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(GraphqlConfig.GraphQlRuntimeHints::class)
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
    fun dateTimeScalarType(): GraphQLScalarType {
        return ExtendedScalars.DateTime
    }

    @Bean
    fun dateScalarType(): GraphQLScalarType {
        return ExtendedScalars.Date
    }

    @Bean
    fun jsonScalarType(): GraphQLScalarType {
        return ExtendedScalars.Json
    }

    @Bean
    fun objectScalarType(): GraphQLScalarType {
        return ExtendedScalars.Object
    }

    @Bean
    fun graphQLBigDecimalScalarType(): GraphQLScalarType {
        return ExtendedScalars.GraphQLBigDecimal
    }

    @Bean
    fun urlScalarType(): GraphQLScalarType {
        return ExtendedScalars.Url
    }

    @Bean
    fun positiveIntScalarType(): GraphQLScalarType {
        return ExtendedScalars.PositiveInt
    }

    @Bean
    fun uuidScalarType(): GraphQLScalarType {
        return ExtendedScalars.UUID
    }

    @Bean
    fun localeScalarType(): GraphQLScalarType {
        return ExtendedScalars.Locale
    }

    @Bean
    fun validationSchemaDirective(): SchemaDirectiveWiring {
        val validationRules = ValidationRules.newValidationRules()
            .onValidationErrorStrategy(OnValidationErrorStrategy.RETURN_NULL)
            .addRule(EmailRule())
            .build()
        return ValidationSchemaWiring(validationRules)
    }

    @Bean
    fun uppercaseDirective(): SchemaDirective {
        return SchemaDirective("uppercase", UppercaseDirective())
    }

    @Bean
    fun lowercaseDirective(): SchemaDirective {
        return SchemaDirective("lowercase", LowercaseDirective())
    }

    @Bean
    fun capitalizeDirective(): SchemaDirective {
        return SchemaDirective("capitalize", CapitalizeDirective())
    }

    @Bean
    fun trimDirective(): SchemaDirective {
        return SchemaDirective("trim", TrimDirective())
    }

    @Bean
    fun graphqlConfigurer(
        graphQLScalarTypes: List<GraphQLScalarType>,
        validationSchemaDirective :SchemaDirectiveWiring,
        schemaDirectives :List<SchemaDirective>
    ): RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer { builder ->
            graphQLScalarTypes.forEach{builder.scalar(it)}
            builder.directiveWiring(validationSchemaDirective)
            schemaDirectives.forEach{builder.directive(it.name, it.directive)}
        }
    }

    internal class GraphQlRuntimeHints : RuntimeHintsRegistrar {
        private val values: Array<MemberCategory> = MemberCategory.values()

        @Suppress("SpreadOperator")
        override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
            setOf(
                GraphQLAppliedDirective::class.java,
                DataFetchingEnvironmentImpl::class.java,
                GraphQlWebSocketMessage::class.java
            ).forEach{hints.reflection().registerType(it, *values)}
            hints.proxies().registerJdkProxy(
                TypeReference.of(
                "graphql.validation.interpolation.ResourceBundleMessageInterpolator\$BridgeAnnotation"))
            hints.resources().registerPattern("graphql/validation/ValidationMessages*.properties")
        }
    }

}
