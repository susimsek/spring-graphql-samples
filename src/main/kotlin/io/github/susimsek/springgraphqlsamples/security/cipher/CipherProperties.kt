package io.github.susimsek.springgraphqlsamples.security.cipher

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@ConfigurationProperties("security.cipher")
@Validated
data class CipherProperties(
    @field:NotBlank
    var base64Secret: String
)
