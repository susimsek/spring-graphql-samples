package io.github.susimsek.springgraphqlsamples.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("cors")
data class CorsProperties(
    var allowedOrigins: MutableList<String> = mutableListOf(),
    var allowedMethods: MutableList<String> = mutableListOf(),
    var allowedHeaders: MutableList<String> = mutableListOf(),
    var exposedHeaders: MutableList<String> = mutableListOf(),
    var allowCredentials: Boolean = false,
    var maxAge: Long = 3600
)
