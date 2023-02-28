package io.github.susimsek.springgraphqlsamples.graphql.input

data class ChangePasswordInput(
    val currentPassword: String,
    val newPassword: String
)
