package io.github.susimsek.springgraphqlsamples.graphql.type

data class Token(
    var token: String = "",
    var tokenType: String = "Bearer",
    var expiresIn: Long = 0
)
