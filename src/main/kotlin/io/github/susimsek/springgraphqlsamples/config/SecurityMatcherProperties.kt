package io.github.susimsek.springgraphqlsamples.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("security-matcher")
data class SecurityMatcherProperties(
    var ignorePatterns: MutableList<String> = mutableListOf("/graphiql", "/favicon.ico"),
    var permitAllPatterns: MutableList<String> = mutableListOf("/actuator/**", "/graphql", "/login")
)
