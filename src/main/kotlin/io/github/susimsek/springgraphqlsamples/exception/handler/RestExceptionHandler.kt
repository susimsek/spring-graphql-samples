package io.github.susimsek.springgraphqlsamples.exception.handler

import io.github.susimsek.springgraphqlsamples.exception.INTERNAL_SERVER_ERROR_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.InvalidTokenException
import io.github.susimsek.springgraphqlsamples.exception.METHOD_ARGUMENT_NOT_VALID_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.METHOD_NOT_ALLOWED_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.NOT_ACCEPTABLE_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.ResourceNotFoundException
import io.github.susimsek.springgraphqlsamples.exception.UNSUPPORTED_MEDIA_TYPE_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.ValidationException
import io.github.susimsek.springgraphqlsamples.exception.model.Problem
import io.github.susimsek.springgraphqlsamples.exception.utils.WebExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.MethodNotAllowedException
import org.springframework.web.server.NotAcceptableStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.UnsupportedMediaTypeStatusException
import reactor.core.publisher.Mono
import java.util.*

@RestControllerAdvice
@Suppress("UnusedPrivateMember")
class RestExceptionHandler(
    private val messageSource: MessageSource,
    private val securityExceptionResolver: ReactiveSecurityExceptionResolver
) : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(javaClass)

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
        val problem = Problem.build(HttpStatus.BAD_REQUEST, errorMessage, exchange)
        problem.createViolations(ex.bindingResult)
        return WebExceptionUtils.buildResponseEntity(problem)
    }

    // 400
    @ExceptionHandler
    fun handleValidationException(
        ex: ValidationException,
        exchange: ServerWebExchange,
        locale: Locale
    ): Mono<ResponseEntity<Any>> {
        val errorMessage = messageSource.getMessage(ex.message!!, ex.args, locale)
        val problem = Problem.build(HttpStatus.BAD_REQUEST, errorMessage, exchange)
        return WebExceptionUtils.buildResponseEntity(problem)
    }

    // 401, 403
    @ExceptionHandler(
        AuthenticationException::class,
        AccessDeniedException::class
    )
    fun handleSecurityException(
        ex: Exception,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> {
        return securityExceptionResolver.resolveException(ex, exchange)
    }

    @ExceptionHandler
    fun handleInvalidTokenException(
        ex: InvalidTokenException,
        locale: Locale,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> {
        val errorMessage = messageSource.getMessage(ex.message!!, ex.args, locale)
        val problem = Problem.build(HttpStatus.UNAUTHORIZED, errorMessage, exchange)
        return WebExceptionUtils.buildResponseEntity(problem)
    }

    // 404
    @ExceptionHandler
    fun handleResourceNotFoundException(
        ex: ResourceNotFoundException,
        locale: Locale,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<Any>> {
        val errorMessage = messageSource.getMessage(ex.message!!, ex.args, locale)
        val problem = Problem.build(HttpStatus.NOT_FOUND, errorMessage, exchange)
        return WebExceptionUtils.buildResponseEntity(problem)
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
        val problem = Problem.build(HttpStatus.METHOD_NOT_ALLOWED, errorMessage, exchange)
        return WebExceptionUtils.buildResponseEntity(problem)
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
        val problem = Problem.build(HttpStatus.NOT_ACCEPTABLE, errorMessage, exchange)
        return WebExceptionUtils.buildResponseEntity(problem)
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
        val problem = Problem.build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, errorMessage, exchange)
        return WebExceptionUtils.buildResponseEntity(problem)
    }

    // 500
    @ExceptionHandler
    fun handleAll(
        ex: Exception,
        locale: Locale,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<Any>> {
        log.error("Internal server error {}", ex.message)
        val errorMessage = messageSource.getMessage(INTERNAL_SERVER_ERROR_MSG_CODE, null, locale)
        val problem = Problem.build(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, exchange)
        return WebExceptionUtils.buildResponseEntity(problem)
    }
}
