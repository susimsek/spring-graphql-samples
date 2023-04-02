package io.github.susimsek.springgraphqlsamples.config

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.validation.annotation.Validated

@ConfigurationProperties("app")
@Validated
data class AppProperties(
    @field:Valid
    @NestedConfigurationProperty
    val mail: Mail
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
