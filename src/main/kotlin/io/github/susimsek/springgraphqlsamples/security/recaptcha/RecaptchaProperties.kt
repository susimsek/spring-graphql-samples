package io.github.susimsek.springgraphqlsamples.security.recaptcha

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("recaptcha")
data class RecaptchaProperties(
    var secretKey: String,
    var verifyUrl: String
)
