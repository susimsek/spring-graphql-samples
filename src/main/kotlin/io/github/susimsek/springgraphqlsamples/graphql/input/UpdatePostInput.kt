package io.github.susimsek.springgraphqlsamples.graphql.input

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdatePostInput(

    @field:NotBlank
    @field:Size(min = 36, max = 36)
    var id: String,

    @field:NotBlank
    @field:Size(min = 3, max = 40)
    var title: String,

    @field:NotBlank
    @field:Size(min = 5, max = 1000)
    var content: String
)
