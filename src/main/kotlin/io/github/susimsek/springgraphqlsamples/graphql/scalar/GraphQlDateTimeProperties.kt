package io.github.susimsek.springgraphqlsamples.graphql.scalar

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "graphql.datetime.scalars")
@ConstructorBinding
data class GraphQlDateTimeProperties(
    var offsetDateTime: ScalarDefinition = ScalarDefinition("OffsetDateTime", null),
    var localDateTime: ScalarDefinition = ScalarDefinition("LocalDateTime", null),
    var localDate: ScalarDefinition = ScalarDefinition("LocalDate", null)
)

data class ScalarDefinition(var scalarName: String?, var format: String?)
