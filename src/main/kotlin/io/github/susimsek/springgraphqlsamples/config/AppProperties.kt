package io.github.susimsek.springgraphqlsamples.config

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@ConfigurationProperties("app")
@Validated
data class AppProperties(
    @field:Valid
    val mail: Mail
)

data class Mail(
    @field:Valid
    var from: MailFrom,
)

data class MailFrom(
    var enabled: Boolean = false,
    @field:Email
    var default: String
)
