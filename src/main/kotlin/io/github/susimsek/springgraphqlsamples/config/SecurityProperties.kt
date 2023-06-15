package io.github.susimsek.springgraphqlsamples.config

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties("security")
data class SecurityProperties(
    @field:Valid
    @NestedConfigurationProperty
    val authentication: Authentication,

    @field:NotBlank
    var contentSecurityPolicy: String
)


data class Authentication(
    @field:Valid
    @NestedConfigurationProperty
    var token: Token,

    @field:Valid
    @NestedConfigurationProperty
    var securityMatcher: SecurityMatcher
)

data class Token(
    var validityInSeconds: Long = 1800L,

    @field:NotBlank
    var publicKey: String,

    @field:NotBlank
    var privateKey: String,

    @field:NotBlank
    var cookieDomain: String
)

data class SecurityMatcher(
    var ignorePatterns: MutableList<String> = mutableListOf(),
    var permitAllPatterns: MutableList<String> = mutableListOf()
)

