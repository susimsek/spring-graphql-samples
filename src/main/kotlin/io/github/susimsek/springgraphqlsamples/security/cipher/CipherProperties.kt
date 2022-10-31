package io.github.susimsek.springgraphqlsamples.security.cipher

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("security.cipher")
data class CipherProperties(
    var base64Secret: String = "c2VjdXJlQ0RDS2V5"
)
