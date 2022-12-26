package io.github.susimsek.springgraphqlsamples.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("security.authentication.token")
data class TokenProperties(
    var tokenValidityInSeconds: Long = 1800L,
    var publicKey: String,
    var privateKey: String
)
