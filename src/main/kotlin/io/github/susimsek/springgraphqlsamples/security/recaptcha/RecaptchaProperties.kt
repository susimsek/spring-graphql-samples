package io.github.susimsek.springgraphqlsamples.security.recaptcha

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@ConfigurationProperties("recaptcha")
@Validated
data class RecaptchaProperties(
    @field:NotBlank
    var secretKey: String,

    @field:NotBlank
    var verifyUrl: String
)
