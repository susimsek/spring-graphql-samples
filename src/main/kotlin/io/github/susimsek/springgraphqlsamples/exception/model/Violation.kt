package io.github.susimsek.springgraphqlsamples.exception.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Violation(
    @field:JsonProperty("object")
    val objectName: String,
    val field: String?,
    val rejectedValue: Any?,
    val message: String
) {
    constructor(objectName: String, message: String) : this(objectName, null, null, message)
}
