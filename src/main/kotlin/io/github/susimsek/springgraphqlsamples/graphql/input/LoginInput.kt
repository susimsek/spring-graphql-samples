package io.github.susimsek.springgraphqlsamples.graphql.input

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginInput(
    @field:NotBlank
    @field:Size(min = 4, max = 50)
    var login: String,

    @field:NotBlank
    @field:Size(min = 4, max = 100)
    var password: String
)
