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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.graphql.client.FieldAccessException
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
import org.springframework.graphql.execution.ErrorType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ControllerAdvice

@ControllerAdvice
@Suppress("UnusedPrivateMember")
class GraphqlExceptionHandler(
    private val messageSource: MessageSource,
    private val securityExceptionResolver: ReactiveSecurityExceptionResolver
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GraphQlExceptionHandler
    suspend fun handleFieldAccessException(
        ex: FieldAccessException,
        env: DataFetchingEnvironment,
    ): GraphQLError = coroutineScope  {
        val responseError = ex.response.errors.first()
        val extensions = responseError.extensions
        val classification = extensions["classification"]
        val errorType = ErrorType.values()
            .firstOrNull { it.name == classification } ?: ErrorType.INTERNAL_ERROR
        GraphqlErrorBuilder.newError(env)
                .message(responseError.message).errorType(errorType).build()
    }

    @GraphQlExceptionHandler(
        AuthenticationException::class,
        AccessDeniedException::class
    )
    suspend fun handleSecurityException(
        ex: Exception,
        env: DataFetchingEnvironment,
    ): GraphQLError = coroutineScope {
        securityExceptionResolver.resolveException(ex, env)
            .awaitSingle()
    }

    @GraphQlExceptionHandler
    suspend fun handleInvalidTokenException(
        ex: InvalidTokenException,
        env: DataFetchingEnvironment,
    ): GraphQLError = coroutineScope {
        val errorMessage = messageSource.getMessage(ex.message!!, ex.args, env.locale)
        GraphqlErrorBuilder.newError(env)
            .message(errorMessage).errorType(ErrorType.UNAUTHORIZED).build()
    }

    @GraphQlExceptionHandler
    suspend fun handleResourceNotFoundException(
        ex: ResourceNotFoundException,
        env: DataFetchingEnvironment,
    ): GraphQLError = coroutineScope {
        val errorMessage = messageSource.getMessage(ex.message!!, ex.args, env.locale)
        GraphqlErrorBuilder.newError(env)
            .message(errorMessage).errorType(ErrorType.NOT_FOUND).build()
    }

    @GraphQlExceptionHandler
    suspend fun handleValidationException(
        ex: ValidationException,
        env: DataFetchingEnvironment,
    ): GraphQLError = coroutineScope {
        val errorMessage = messageSource.getMessage(ex.message!!, ex.args, env.locale)
        badRequest(env, errorMessage)
    }

    @GraphQlExceptionHandler
    suspend fun handleRateLimitingException(
        ex: RateLimitingException,
        env: DataFetchingEnvironment,
    ): GraphQLError = coroutineScope {
        val errorMessage = messageSource.getMessage(TOO_MANY_REQUESTS_MSG_CODE, null, env.locale)
        GraphqlErrorBuilder.newError(env)
            .message(errorMessage).errorType(ExtendedErrorType.THROTTLED).build()
    }

    @GraphQlExceptionHandler
    suspend fun handleAll(
        ex: Exception,
        env: DataFetchingEnvironment,
    ): GraphQLError = coroutineScope {
        log.error("Internal server error {}", ex.message)
        val errorMessage = messageSource.getMessage(INTERNAL_SERVER_ERROR_MSG_CODE, null, env.locale)
            GraphqlErrorBuilder.newError(env)
                .message(errorMessage).errorType(ErrorType.INTERNAL_ERROR).build()
    }

    @GraphQlExceptionHandler
    suspend fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        env: DataFetchingEnvironment,
    ): List<GraphQLError> = coroutineScope {
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
    }

    private fun badRequest(env: DataFetchingEnvironment, errorMessage: String): GraphQLError {
        return GraphqlErrorBuilder.newError(env)
            .message(errorMessage).errorType(ErrorType.BAD_REQUEST).build()
    }
}
