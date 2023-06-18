package io.github.susimsek.springgraphqlsamples.exception.handler

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import io.github.susimsek.springgraphqlsamples.exception.*
import jakarta.validation.ConstraintViolationException
import org.springframework.context.MessageSource
import org.springframework.graphql.client.FieldAccessException
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
import org.springframework.graphql.execution.ErrorType
import org.springframework.web.bind.annotation.ControllerAdvice
import java.lang.Exception

@ControllerAdvice
class GraphqlExceptionHandler(
    private val messageSource: MessageSource
) {

    @GraphQlExceptionHandler
    fun handleFieldAccessException(
        ex: FieldAccessException,
        env: DataFetchingEnvironment
    ): GraphQLError {
        val responseError = ex.response.errors.first()
        val extensions = responseError.extensions
        val classification = extensions["classification"]
        val errorType = ErrorType.values()
            .firstOrNull { it.name == classification } ?: ErrorType.INTERNAL_ERROR
        return GraphqlErrorBuilder.newError(env)
            .message(responseError.message).errorType(errorType).build()
    }

    @GraphQlExceptionHandler
    fun handleResourceNotFoundException(
        ex: ResourceNotFoundException,
        env: DataFetchingEnvironment
    ): GraphQLError {
        val errorMessage = messageSource.getMessage(ex.message!!, ex.args, env.locale)
        return GraphqlErrorBuilder.newError(env)
            .message(errorMessage).errorType(ErrorType.NOT_FOUND).build()
    }

    @GraphQlExceptionHandler
    fun handleValidationException(
        ex: ValidationException,
        env: DataFetchingEnvironment
    ): GraphQLError {
        val errorMessage = messageSource.getMessage(ex.message!!, ex.args, env.locale)
        return badRequest(env, errorMessage)
    }

    @GraphQlExceptionHandler
    fun handleRateLimitingException(
        ex: RateLimitingException,
        env: DataFetchingEnvironment
    ): GraphQLError {
        val errorMessage = messageSource.getMessage(TOO_MANY_REQUESTS_MSG_CODE, null, env.locale)
        return GraphqlErrorBuilder.newError(env)
            .message(errorMessage).errorType(ExtendedErrorType.THROTTLED).build()
    }

    // 500
    @GraphQlExceptionHandler
    fun handleAll(
        ex: Exception,
        env: DataFetchingEnvironment
    ): GraphQLError {
        val errorMessage = messageSource.getMessage(INTERNAL_SERVER_ERROR_MSG_CODE, null, env.locale)
        return GraphqlErrorBuilder.newError(env)
            .message(errorMessage).errorType(ErrorType.INTERNAL_ERROR).build()
    }

    @GraphQlExceptionHandler
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        env: DataFetchingEnvironment
    ): List<GraphQLError> {
        return ex.constraintViolations.map {
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
