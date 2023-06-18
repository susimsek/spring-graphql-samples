package io.github.susimsek.springgraphqlsamples.exception.handler

import io.github.susimsek.springgraphqlsamples.exception.METHOD_ARGUMENT_NOT_VALID_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.ResourceNotFoundException
import io.github.susimsek.springgraphqlsamples.exception.model.ApiError
import org.springframework.context.MessageSource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

@ControllerAdvice
class RestExceptionHandler(
    private val messageSource: MessageSource,
) : ResponseEntityExceptionHandler() {

    override fun handleWebExchangeBindException(
        ex: WebExchangeBindException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<Any>> {
        val errorMessage = messageSource.getMessage(
            METHOD_ARGUMENT_NOT_VALID_MSG_CODE,
            null,
            exchange.localeContext.locale ?: Locale.getDefault()
        )
        val apiError = buildApiError(HttpStatus.BAD_REQUEST, errorMessage, exchange)
        apiError.addFieldErrors(ex.fieldErrors)
        apiError.addGlobalErrors(ex.globalErrors)
        return buildResponseEntity(apiError)
    }

    @ExceptionHandler
    fun handleResourceNotFoundException(
        ex: ResourceNotFoundException,
        locale: Locale,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> {
        val errorMessage = messageSource.getMessage(ex.message!!, ex.args, locale)
        val apiError = buildApiError(HttpStatus.NOT_FOUND, errorMessage, exchange)
        return buildResponseEntity(apiError)
    }

    private fun buildApiError(status: HttpStatus, message: String, exchange: ServerWebExchange): ApiError {
        return ApiError(status, message, exchange.request.uri.path)
    }

    private fun buildResponseEntity(apiError: ApiError): Mono<ResponseEntity<Any>> {
        return Mono.just(ResponseEntity(apiError, apiError.status))
    }
}
