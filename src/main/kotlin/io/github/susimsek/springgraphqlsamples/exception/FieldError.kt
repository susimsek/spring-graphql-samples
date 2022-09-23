package io.github.susimsek.springgraphqlsamples.exception

data class FieldError(
    var property: String? = null,
    var message: String? = null
)
