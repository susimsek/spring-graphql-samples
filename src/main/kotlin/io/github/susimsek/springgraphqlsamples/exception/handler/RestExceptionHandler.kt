package io.github.susimsek.springgraphqlsamples.exception.handler

import io.github.susimsek.springgraphqlsamples.exception.*
import io.github.susimsek.springgraphqlsamples.exception.model.ApiError
import org.springframework.context.MessageSource
import org.springframework.http.*
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.MethodNotAllowedException
import org.springframework.web.server.NotAcceptableStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.UnsupportedMediaTypeStatusException
import reactor.core.publisher.Mono
import java.util.*

@ControllerAdvice
class RestExceptionHandler(
    private val messageSource: MessageSource,
) : ResponseEntityExceptionHandler() {

    // 400
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

    // 404
    @ExceptionHandler
    fun handleResourceNotFoundException(
        ex: ResourceNotFoundException,
        locale: Locale,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<Any>> {
        val errorMessage = messageSource.getMessage(ex.message!!, ex.args, locale)
        val apiError = buildApiError(HttpStatus.NOT_FOUND, errorMessage, exchange)
        return buildResponseEntity(apiError)
    }

    // 405
    override fun handleMethodNotAllowedException(
        ex: MethodNotAllowedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<Any>> {
        val errorMessage = messageSource.getMessage(
            METHOD_NOT_ALLOWED_MSG_CODE,
            arrayOf(
                ex.httpMethod,
                ex.supportedMethods.joinToString(prefix = "[", postfix = "]", separator = ", ")
            ),
            exchange.localeContext.locale ?: Locale.getDefault()
        )
        val apiError = buildApiError(HttpStatus.METHOD_NOT_ALLOWED, errorMessage, exchange)
        return buildResponseEntity(apiError)
    }

    // 406
    override fun handleNotAcceptableStatusException(
        ex: NotAcceptableStatusException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<Any>> {
        val errorMessage = messageSource.getMessage(
            NOT_ACCEPTABLE_MSG_CODE,
            null,
            exchange.localeContext.locale ?: Locale.getDefault()
        )
        val apiError = buildApiError(HttpStatus.NOT_ACCEPTABLE, errorMessage, exchange)
        return buildResponseEntity(apiError)
    }

    // 415
    override fun handleUnsupportedMediaTypeStatusException(
        ex: UnsupportedMediaTypeStatusException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<Any>> {
        val errorMessage = messageSource.getMessage(
            UNSUPPORTED_MEDIA_TYPE_MSG_CODE,
            arrayOf(
                ex.contentType,
                ex.supportedMediaTypes.joinToString(prefix = "[", postfix = "]", separator = ", ")
            ),
            exchange.localeContext.locale ?: Locale.getDefault()
        )
        val apiError = buildApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, errorMessage, exchange)
        return buildResponseEntity(apiError)
    }

    // 500
    @ExceptionHandler
    fun handleAll(
        ex: Exception,
        locale: Locale,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<Any>> {
        val errorMessage = messageSource.getMessage(INTERNAL_SERVER_ERROR_MSG_CODE, null, locale)
        val apiError = buildApiError(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, exchange)
        return buildResponseEntity(apiError)
    }

    private fun buildApiError(status: HttpStatus, message: String, exchange: ServerWebExchange): ApiError {
        return ApiError(status, message, exchange.request.uri.path)
    }

    private fun buildResponseEntity(apiError: ApiError): Mono<ResponseEntity<Any>> {
        return Mono.just(
            ResponseEntity.status(apiError.status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiError))
    }
}
