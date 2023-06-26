package io.github.susimsek.springgraphqlsamples.config

import graphql.scalars.ExtendedScalars
import graphql.schema.GraphQLScalarType
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.validation.rules.OnValidationErrorStrategy
import graphql.validation.rules.ValidationRules
import graphql.validation.schemawiring.ValidationSchemaWiring
import io.github.susimsek.springgraphqlsamples.graphql.GraphqlSortStrategy
import io.github.susimsek.springgraphqlsamples.graphql.directive.CapitalizeDirective
import io.github.susimsek.springgraphqlsamples.graphql.directive.LowercaseDirective
import io.github.susimsek.springgraphqlsamples.graphql.directive.SchemaDirective
import io.github.susimsek.springgraphqlsamples.graphql.directive.TrimDirective
import io.github.susimsek.springgraphqlsamples.graphql.directive.UppercaseDirective
import io.github.susimsek.springgraphqlsamples.graphql.validation.EmailRule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.data.query.SortStrategy
import org.springframework.graphql.execution.RuntimeWiringConfigurer

@Configuration(proxyBeanMethods = false)
class GraphqlConfig {

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
    fun bigDecimalScalarType(): GraphQLScalarType {
        return ExtendedScalars.GraphQLBigDecimal
    }

    @Bean
    fun urlScalarType(): GraphQLScalarType {
        return ExtendedScalars.Url
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
    fun sortStrategy(): SortStrategy {
        return GraphqlSortStrategy()
    }

    @Bean
    fun graphqlConfigurer(
        graphQLScalarTypes: List<GraphQLScalarType>,
        validationSchemaDirective: SchemaDirectiveWiring,
        schemaDirectives: List<SchemaDirective>
    ): RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer { builder ->
            graphQLScalarTypes.forEach { builder.scalar(it) }
            builder.directiveWiring(validationSchemaDirective)
            schemaDirectives.forEach { builder.directive(it.name, it.directive) }
        }
    }
}
