package io.github.susimsek.springgraphqlsamples.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("security.authentication.security-matcher")
data class SecurityMatcherProperties(
    var ignorePatterns: MutableList<String> = mutableListOf(),
    var permitAllPatterns: MutableList<String> = mutableListOf()
)
