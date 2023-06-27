package io.github.susimsek.springgraphqlsamples.exception.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Violation(
    @Schema(description = "Object name", example = "loginRequest")
    @field:JsonProperty("object")
    val objectName: String,
    @Schema(description = "Field name", example = "login")
    val field: String?,
    @Schema(description = "Violation rejected value", example = "a")
    val rejectedValue: Any?,
    @Schema(description = "Error Message", example = "size must be between 4 and 50")
    val message: String
) {
    constructor(objectName: String, message: String) : this(objectName, null, null, message)
}
