package io.github.susimsek.springgraphqlsamples.graphql.input

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

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
