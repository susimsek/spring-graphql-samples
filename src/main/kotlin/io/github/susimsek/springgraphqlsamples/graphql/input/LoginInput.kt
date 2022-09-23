package io.github.susimsek.springgraphqlsamples.graphql.input

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class LoginInput(
    @field:NotBlank
    @field:Size(min = 4, max = 50)
    var login: String? = null,

    @field:NotBlank
    @field:Size(min = 4, max = 100)
    var password: String? = null
)
