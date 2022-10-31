package io.github.susimsek.springgraphqlsamples.graphql.input

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AddUserInput(
    @field:NotBlank
    @field:Size(min = 4, max = 50)
    var username: String? = null,

    @field:NotBlank
    @field:Size(min = 4, max = 100)
    var password: String? = null,

    @field:NotBlank
    @field:Size(max = 50)
    var firstName: String? = null,

    @field:NotBlank
    @field:Size(max = 50)
    var lastName: String? = null,

    @field:Email
    @field:NotBlank
    @field:Size(min = 5, max = 254)
    var email: String? = null
)
