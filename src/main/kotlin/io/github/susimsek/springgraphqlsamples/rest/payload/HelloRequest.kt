package io.github.susimsek.springgraphqlsamples.rest.payload

import jakarta.validation.constraints.NotNull

data class HelloRequest(
    var enabled: Boolean,
    @field:NotNull
    var gender: Gender
)
