package io.github.susimsek.springgraphqlsamples.rest.payload

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:NotBlank
    @field:Size(min = 4, max = 50)
    val login: String,
    @field:NotBlank
    @field:Size(min = 4, max = 100)
    val password: String
)
