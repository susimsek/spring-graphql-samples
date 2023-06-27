package io.github.susimsek.springgraphqlsamples.exception.handler

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import io.github.susimsek.springgraphqlsamples.exception.FORBIDDEN_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.UNAUTHORIZED_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.model.Problem
import io.github.susimsek.springgraphqlsamples.exception.utils.WebExceptionUtils
import org.springframework.context.MessageSource
import org.springframework.graphql.execution.ErrorType
import org.springframework.http.HttpStatus
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
    private val mapper: ObjectMapper
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
            WebExceptionUtils.buildResponseEntity(unauthorized(exchange))
        } else {
            ReactiveSecurityContextHolder.getContext()
                .flatMap { context -> WebExceptionUtils.buildResponseEntity(accessDenied(exchange, context)) }
                .switchIfEmpty { WebExceptionUtils.buildResponseEntity(unauthorized(exchange)) }
        }
    }

    private fun unauthorized(env: DataFetchingEnvironment): GraphQLError {
        val errorMessage = messageSource.getMessage(UNAUTHORIZED_MSG_CODE, null, env.locale)
        return GraphqlErrorBuilder.newError(env).errorType(ErrorType.UNAUTHORIZED)
            .message(errorMessage).build()
    }

    private fun unauthorized(exchange: ServerWebExchange): Problem {
        val errorMessage = messageSource.getMessage(
            UNAUTHORIZED_MSG_CODE,
            null,
            resolveLocale(exchange)
        )
        return Problem.build(HttpStatus.UNAUTHORIZED, errorMessage, exchange)
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
    ): Problem {
        return if (trustResolver.isAnonymous(securityContext.authentication)) {
            unauthorized(exchange)
        } else {
            accessDenied(exchange)
        }
    }

    private fun accessDenied(exchange: ServerWebExchange): Problem {
        val errorMessage = messageSource.getMessage(
            FORBIDDEN_MSG_CODE,
            null,
            resolveLocale(exchange)
        )
        return Problem.build(HttpStatus.FORBIDDEN, errorMessage, exchange)
    }

    private fun resolveLocale(exchange: ServerWebExchange): Locale {
        return exchange.localeContext.locale ?: Locale.getDefault()
    }

    @Suppress("kotlin:S6508")
    override fun commence(exchange: ServerWebExchange, ex: AuthenticationException): Mono<Void> {
        return resolveException(ex, exchange)
            .flatMap { WebExceptionUtils.setHttpResponse(exchange, unauthorized(exchange), mapper) }
    }

    @Suppress("kotlin:S6508")
    override fun handle(exchange: ServerWebExchange, denied: AccessDeniedException): Mono<Void> {
        return resolveException(denied, exchange)
            .flatMap { WebExceptionUtils.setHttpResponse(exchange, accessDenied(exchange), mapper) }
    }
}
