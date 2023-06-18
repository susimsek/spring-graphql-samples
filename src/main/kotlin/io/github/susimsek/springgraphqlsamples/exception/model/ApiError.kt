package io.github.susimsek.springgraphqlsamples.exception.model

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import java.time.OffsetDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiError(
    val status: HttpStatus,
    val timestamp: OffsetDateTime,
    val message: String,
    val path: String,
    var fieldErrors: MutableList<ApiFieldError>? = null,
) {
    constructor(status: HttpStatus, message: String, path: String) : this(
        status,
        OffsetDateTime.now(),
        message,
        path
    )

    fun addFieldErrors(fieldErrors: List<FieldError>) {
        val validationErrors = fieldErrors.map(this::mapFieldError)
        addApiFieldErrors(validationErrors)
    }

    fun addGlobalErrors(globalErrors: List<ObjectError>) {
        val validationErrors = globalErrors.map(this::mapFieldError)
        addApiFieldErrors(validationErrors)
    }

    private fun addApiFieldErrors(fieldErrors: List<ApiFieldError>) {
        if (this.fieldErrors == null) {
            this.fieldErrors = mutableListOf()
        }
        this.fieldErrors!!.addAll(fieldErrors)
    }

    private fun mapFieldError(fieldError: FieldError): ApiFieldError {
        return ApiFieldError(
            fieldError.objectName,
            fieldError.field,
            fieldError.rejectedValue ?: "",
            fieldError.defaultMessage ?: ""
        )
    }

    private fun mapFieldError(objectError: ObjectError): ApiFieldError {
        return ApiFieldError(
            objectError.objectName,
            objectError.defaultMessage ?: ""
        )
    }
}
