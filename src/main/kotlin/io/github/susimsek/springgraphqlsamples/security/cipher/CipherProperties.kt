package io.github.susimsek.springgraphqlsamples.security.cipher

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("security.cipher")
@ConstructorBinding
data class CipherProperties(
    var base64Secret: String = "c2VjdXJlQ0RDS2V5"
)
