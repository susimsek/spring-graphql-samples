package io.github.susimsek.springgraphqlsamples.exception.model

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.server.ServerWebExchange
import java.time.OffsetDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiError(
    val status: HttpStatus,
    val timestamp: OffsetDateTime,
    val message: String,
    val path: String,
    var fieldErrors: MutableList<Violation>? = null,
) {
    constructor(status: HttpStatus, message: String, path: String) : this(
        status,
        OffsetDateTime.now(),
        message,
        path
    )

    companion object {
        fun build(status: HttpStatus, message: String, exchange: ServerWebExchange): ApiError {
            return ApiError(status, message, exchange.request.uri.path)
        }
    }

    fun addFieldErrors(fieldErrors: List<FieldError>) {
        val validationErrors = fieldErrors.map(this::mapFieldError)
        addViolations(validationErrors)
    }

    fun addGlobalErrors(globalErrors: List<ObjectError>) {
        val validationErrors = globalErrors.map(this::mapObjectError)
        addViolations(validationErrors)
    }

    private fun addViolations(fieldErrors: List<Violation>) {
        if (this.fieldErrors == null) {
            this.fieldErrors = mutableListOf()
        }
        this.fieldErrors!!.addAll(fieldErrors)
    }

    private fun mapFieldError(fieldError: FieldError): Violation {
        return Violation(
            fieldError.objectName,
            fieldError.field,
            fieldError.rejectedValue ?: "",
            fieldError.defaultMessage ?: ""
        )
    }

    private fun mapObjectError(objectError: ObjectError): Violation {
        return Violation(
            objectError.objectName,
            objectError.defaultMessage ?: ""
        )
    }
}
