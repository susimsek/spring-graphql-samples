package io.github.susimsek.springgraphqlsamples.security.jwt

data class Token(
    var token: String,
    var expiresIn: Long
)
