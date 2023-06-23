package io.github.susimsek.springgraphqlsamples.graphql.type

data class TokenPayload(
    var accessToken: String = "",
    var refreshToken: String = "",
    val tokenType: String = "Bearer",
    var accessTokenExpiresIn: Long = 0,
    var refreshTokenExpiresIn: Long = 0
)
