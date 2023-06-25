package io.github.susimsek.springgraphqlsamples.exception.handler

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import io.github.susimsek.springgraphqlsamples.exception.ExtendedErrorType
import io.github.susimsek.springgraphqlsamples.exception.INTERNAL_SERVER_ERROR_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.InvalidTokenException
import io.github.susimsek.springgraphqlsamples.exception.RateLimitingException
import io.github.susimsek.springgraphqlsamples.exception.ResourceNotFoundException
import io.github.susimsek.springgraphqlsamples.exception.TOO_MANY_REQUESTS_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.ValidationException
import jakarta.validation.ConstraintViolationException
import org.springframework.context.MessageSource
import org.springframework.graphql.client.FieldAccessException
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
import org.springframework.graphql.execution.ErrorType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ControllerAdvice
import reactor.core.publisher.Mono

@ControllerAdvice
@Suppress("UnusedPrivateMember")
class GraphqlExceptionHandler(
    private val messageSource: MessageSource,
    private val securityExceptionResolver: ReactiveSecurityExceptionResolver
) {

    @GraphQlExceptionHandler
    fun handleFieldAccessException(
        ex: FieldAccessException,
        env: DataFetchingEnvironment,
    ): Mono<GraphQLError> {
        val responseError = ex.response.errors.first()
        val extensions = responseError.extensions
        val classification = extensions["classification"]
        val errorType = ErrorType.values()
            .firstOrNull { it.name == classification } ?: ErrorType.INTERNAL_ERROR
        return Mono.just(
            GraphqlErrorBuilder.newError(env)
                .message(responseError.message).errorType(errorType).build()
        )
    }

    @GraphQlExceptionHandler(
        AuthenticationException::class,
        AccessDeniedException::class
    )
    fun handleSecurityException(
        ex: Exception,
        env: DataFetchingEnvironment,
    ): Mono<GraphQLError> {
        return securityExceptionResolver.resolveException(ex, env)
    }

    @GraphQlExceptionHandler
    fun handleInvalidTokenException(
        ex: InvalidTokenException,
        env: DataFetchingEnvironment,
    ): Mono<GraphQLError> {
        val errorMessage = messageSource.getMessage(ex.message!!, ex.args, env.locale)
        return Mono.just(
            GraphqlErrorBuilder.newError(env)
                .message(errorMessage).errorType(ErrorType.UNAUTHORIZED).build()
        )
    }

    @GraphQlExceptionHandler
    fun handleResourceNotFoundException(
        ex: ResourceNotFoundException,
        env: DataFetchingEnvironment,
    ): Mono<GraphQLError> {
        val errorMessage = messageSource.getMessage(ex.message!!, ex.args, env.locale)
        return Mono.just(
            GraphqlErrorBuilder.newError(env)
                .message(errorMessage).errorType(ErrorType.NOT_FOUND).build()
        )
    }

    @GraphQlExceptionHandler
    fun handleValidationException(
        ex: ValidationException,
        env: DataFetchingEnvironment,
    ): Mono<GraphQLError> {
        val errorMessage = messageSource.getMessage(ex.message!!, ex.args, env.locale)
        return Mono.just(badRequest(env, errorMessage))
    }

    @GraphQlExceptionHandler
    fun handleRateLimitingException(
        ex: RateLimitingException,
        env: DataFetchingEnvironment,
    ): Mono<GraphQLError> {
        val errorMessage = messageSource.getMessage(TOO_MANY_REQUESTS_MSG_CODE, null, env.locale)
        return Mono.just(
            GraphqlErrorBuilder.newError(env)
                .message(errorMessage).errorType(ExtendedErrorType.THROTTLED).build()
        )
    }

    @GraphQlExceptionHandler
    fun handleAll(
        ex: Exception,
        env: DataFetchingEnvironment,
    ): Mono<GraphQLError> {
        println("ex=" + ex.message)
        val errorMessage = messageSource.getMessage(INTERNAL_SERVER_ERROR_MSG_CODE, null, env.locale)
        return Mono.just(
            GraphqlErrorBuilder.newError(env)
                .message(errorMessage).errorType(ErrorType.INTERNAL_ERROR).build()
        )
    }

    @GraphQlExceptionHandler
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        env: DataFetchingEnvironment,
    ): Mono<List<GraphQLError>> {
        return Mono.just(
            ex.constraintViolations.map {
                val validatedPath = it.propertyPath.map { node -> node.name }
                GraphqlErrorBuilder.newError(env)
                    .message("${it.propertyPath}: ${it.message}")
                    .errorType(ErrorType.BAD_REQUEST)
                    .extensions(
                        mapOf(
                            "validatedPath" to validatedPath
                        )
                    )
                    .build()
            }
        )
    }

    private fun badRequest(env: DataFetchingEnvironment, errorMessage: String): GraphQLError {
        return GraphqlErrorBuilder.newError(env)
            .message(errorMessage).errorType(ErrorType.BAD_REQUEST).build()
    }
}
