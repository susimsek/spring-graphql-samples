package io.github.susimsek.springgraphqlsamples.security.jwt

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@ConfigurationProperties("security.authentication.token")
@Validated
data class TokenProperties(
    var tokenValidityInSeconds: Long = 1800L,

    @field:NotBlank
    var publicKey: String,

    @field:NotBlank
    var privateKey: String
)
