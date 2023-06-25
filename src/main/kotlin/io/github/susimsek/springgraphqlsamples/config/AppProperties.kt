package io.github.susimsek.springgraphqlsamples.config

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import org.hibernate.validator.constraints.URL
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.validation.annotation.Validated

@ConfigurationProperties("app")
@Validated
data class AppProperties(
    @field:Valid
    @NestedConfigurationProperty
    val mail: Mail,

    @field:Valid
    @NestedConfigurationProperty
    val cache: Cache
)

data class Mail(
    @field:Valid
    @NestedConfigurationProperty
    var from: MailFrom,

    @field:NotBlank
    @field:URL
    var baseUrl: String
)

data class MailFrom(
    var enabled: Boolean = false,
    @field:Email
    var default: String
)

data class Cache(
    @field:Valid
    @NestedConfigurationProperty
    var redis: Redis
)

data class Redis(
    @field:NotNull
    @field:PositiveOrZero
    var timeToLiveSeconds: Long,
    @field:Valid
    @NestedConfigurationProperty
    var serializer: RedisSerializer
)

data class RedisSerializer(
    @field:NotNull
    var valueType: RedisSerializerType
)

enum class RedisSerializerType {
    JDK,
    JSON
}
