package io.github.susimsek.springgraphqlsamples.exception.model

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
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
    var violations: MutableList<Violation>? = null,
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

    fun createViolations(result: BindingResult) {
        val fieldErrors = result.fieldErrors.map(this::createViolation)
        val globalErrors = result.globalErrors.map(this::createViolation)
        addViolations(fieldErrors)
        addViolations(globalErrors)
    }

    private fun addViolations(fieldErrors: List<Violation>) {
        if (this.violations == null) {
            this.violations = mutableListOf()
        }
        this.violations!!.addAll(fieldErrors)
    }

    private fun createViolation(fieldError: FieldError): Violation {
        return Violation(
            fieldError.objectName,
            fieldError.field,
            fieldError.rejectedValue ?: "",
            fieldError.defaultMessage ?: ""
        )
    }

    private fun createViolation(objectError: ObjectError): Violation {
        return Violation(
            objectError.objectName,
            objectError.defaultMessage ?: ""
        )
    }
}
