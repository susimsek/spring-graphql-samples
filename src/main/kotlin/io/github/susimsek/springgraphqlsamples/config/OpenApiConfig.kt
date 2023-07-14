package io.github.susimsek.springgraphqlsamples.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.parameters.Parameter
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@OpenAPIDefinition(
    info = Info(
        title = "Rest Fullstack Samples API",
        description = "Rest Fullstack Samples API documentation",
        version = "v1",
        license = License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        ),
        termsOfService = "http://swagger.io/terms/"
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
@Configuration(proxyBeanMethods = false)
class OpenApiConfig {

    @Bean
    fun customize(): OperationCustomizer {
        val languageSchema = StringSchema()
        languageSchema.enum = listOf("en", "tr")
        val languageParameter = Parameter()
            .name("Accept-Language")
            .description("Language preference")
            .schema(languageSchema)
            .example("en")
            .`in`("header")
            .required(true)
        return OperationCustomizer { operation: Operation, _ ->
            operation.addParametersItem(
                languageParameter
            )
        }
    }
}
