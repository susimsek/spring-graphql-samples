package io.github.susimsek.springgraphqlsamples.graphql.input

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AddPostInput(
    @field:NotBlank
    @field:Size(min = 3, max = 40)
    val title: String = "",

    @field:NotBlank
    @field:Size(min = 5, max = 1000)
    val content: String = ""
)
