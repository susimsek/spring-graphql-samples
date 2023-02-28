package io.github.susimsek.springgraphqlsamples.graphql.input

data class ResetPasswordInput(
    val token: String,
    val newPassword: String
)
