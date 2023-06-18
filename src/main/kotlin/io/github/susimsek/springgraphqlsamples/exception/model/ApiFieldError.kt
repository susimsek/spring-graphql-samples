package io.github.susimsek.springgraphqlsamples.exception.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiFieldError(
    val `object`: String,
    val field: String?,
    val rejectedValue: Any?,
    val message: String
) {
    constructor(`object`: String, message: String) : this(`object`, null, null, message)
}
