package io.github.susimsek.springgraphqlsamples.security.recaptcha

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@ConfigurationProperties("recaptcha")
@Validated
data class RecaptchaProperties(
    var enabled: Boolean = true,

    @field:NotBlank
    var secretKey: String,

    @field:NotBlank
    var verifyUrl: String,

    @field:DecimalMin("0.0")
    @field:DecimalMax("1.0")
    var threshold: Float
)
