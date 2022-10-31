package io.github.susimsek.springgraphqlsamples.graphql.input

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdatePostInput(

    @field:NotBlank
    @field:Size(min = 24, max = 24)
    var id: String? = null,

    @field:NotBlank
    @field:Size(min = 5, max = 100)
    var title: String? = null,

    @field:NotBlank
    @field:Size(min = 5, max = 1000)
    var content: String? = null
)
