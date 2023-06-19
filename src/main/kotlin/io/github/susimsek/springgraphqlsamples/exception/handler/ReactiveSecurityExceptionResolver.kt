package io.github.susimsek.springgraphqlsamples.exception.handler

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import io.github.susimsek.springgraphqlsamples.exception.FORBIDDEN_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.UNAUTHORIZED_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.model.ApiError
import org.springframework.context.MessageSource
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.graphql.execution.ErrorType
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationTrustResolver
import org.springframework.security.authentication.AuthenticationTrustResolverImpl
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.util.*

@Component
class ReactiveSecurityExceptionResolver(
    private val messageSource: MessageSource,
    private val objectMapper: ObjectMapper
) : ServerAuthenticationEntryPoint, ServerAccessDeniedHandler {

    private val trustResolver: AuthenticationTrustResolver = AuthenticationTrustResolverImpl()

    fun resolveException(
        ex: Throwable,
        env: DataFetchingEnvironment,
    ): Mono<GraphQLError> {
        return if (ex is AuthenticationException) {
            Mono.just(unauthorized(env))
        } else {
            ReactiveSecurityContextHolder.getContext().map { context -> accessDenied(env, context) }
                .switchIfEmpty { Mono.fromCallable { unauthorized(env) } }
        }
    }

    fun resolveException(
        ex: Throwable,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> {
        return if (ex is AuthenticationException) {
            buildResponseEntity(unauthorized(exchange))
        } else {
            ReactiveSecurityContextHolder.getContext()
                .flatMap { context -> buildResponseEntity(accessDenied(exchange, context)) }
                .switchIfEmpty { buildResponseEntity(unauthorized(exchange)) }
        }
    }

    private fun unauthorized(env: DataFetchingEnvironment): GraphQLError {
        val errorMessage = messageSource.getMessage(UNAUTHORIZED_MSG_CODE, null, env.locale)
        return GraphqlErrorBuilder.newError(env).errorType(ErrorType.UNAUTHORIZED)
            .message(errorMessage).build()
    }

    private fun unauthorized(exchange: ServerWebExchange): ApiError {
        val errorMessage = messageSource.getMessage(
            UNAUTHORIZED_MSG_CODE,
            null,
            resolveLocale(exchange)
        )
        return ApiError.build(HttpStatus.UNAUTHORIZED, errorMessage, exchange)
    }

    private fun accessDenied(
        env: DataFetchingEnvironment,
        securityContext: SecurityContext,
    ): GraphQLError {
        return if (trustResolver.isAnonymous(securityContext.authentication)) {
            unauthorized(env)
        } else {
            val errorMessage = messageSource.getMessage(FORBIDDEN_MSG_CODE, null, env.locale)
            GraphqlErrorBuilder.newError(env).errorType(ErrorType.FORBIDDEN)
                .message(errorMessage).build()
        }
    }

    private fun accessDenied(
        exchange: ServerWebExchange,
        securityContext: SecurityContext
    ): ApiError {
        return if (trustResolver.isAnonymous(securityContext.authentication)) {
            unauthorized(exchange)
        } else {
            accessDenied(exchange)
        }
    }

    private fun accessDenied(exchange: ServerWebExchange): ApiError {
        val errorMessage = messageSource.getMessage(
            FORBIDDEN_MSG_CODE,
            null,
            resolveLocale(exchange)
        )
        return ApiError.build(HttpStatus.FORBIDDEN, errorMessage, exchange)
    }

    private fun resolveLocale(exchange: ServerWebExchange): Locale {
        return exchange.localeContext.locale ?: Locale.getDefault()
    }

    private fun buildResponseEntity(apiError: ApiError): Mono<ResponseEntity<Any>> {
        return Mono.just(
            ResponseEntity.status(apiError.status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiError)
        )
    }

    override fun commence(exchange: ServerWebExchange, ex: AuthenticationException): Mono<Void> {
        return writeResponse(exchange, unauthorized(exchange))
    }

    override fun handle(exchange: ServerWebExchange, denied: AccessDeniedException): Mono<Void> {
        return writeResponse(exchange, accessDenied(exchange))
    }

    private fun writeResponse(exchange: ServerWebExchange, apiError: ApiError): Mono<Void> {
        val dataBuffer = DefaultDataBufferFactory().wrap(objectMapper.writeValueAsBytes(apiError))
        exchange.response.headers.contentType = MediaType.APPLICATION_JSON
        return exchange.response.writeWith(Mono.just(dataBuffer))
    }
}
