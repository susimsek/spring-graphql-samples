package io.github.susimsek.springgraphqlsamples.exception

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import jakarta.validation.ConstraintViolationException
import org.springframework.context.MessageSource
import org.springframework.graphql.client.FieldAccessException
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.graphql.execution.ErrorType
import org.springframework.stereotype.Component

@Component
class ReactiveGraphqlExceptionResolver(
    private val messageSource: MessageSource
) : DataFetcherExceptionResolverAdapter() {
    override fun resolveToSingleError(
        ex: Throwable,
        env: DataFetchingEnvironment
    ): GraphQLError? {
        val locale = env.locale
        return when (ex) {
            is FieldAccessException -> {
                val responseError = ex.response.errors.first()
                val extensions = responseError.extensions
                val classification = extensions["classification"]
                val errorType = ErrorType.values()
                    .firstOrNull { it.name == classification } ?: ErrorType.INTERNAL_ERROR
                return GraphqlErrorBuilder.newError(env)
                    .message(responseError.message).errorType(errorType).build()
            }

            is ResourceNotFoundException -> {
                val errorMessage = messageSource.getMessage(ex.message, ex.args, locale)
                return GraphqlErrorBuilder.newError(env)
                    .message(errorMessage).errorType(ErrorType.NOT_FOUND).build()
            }

            is ValidationException -> {
                val errorMessage = messageSource.getMessage(ex.message, ex.args, locale)
                return GraphqlErrorBuilder.newError(env)
                    .message(errorMessage).errorType(ErrorType.BAD_REQUEST).build()
            }

            is ConstraintViolationException -> {
                val errors = ex.constraintViolations.map {
                    FieldError(
                        property = it.propertyPath.reduce { _, second -> second }.toString(),
                        message = it.message
                    )
                }

                val errorMessage = messageSource.getMessage("error.constraint.violation.message", null, locale)

                return GraphqlErrorBuilder.newError(env)
                    .message(errorMessage).errorType(graphql.ErrorType.ValidationError)
                    .extensions(mapOf("errors" to errors))
                    .build()
            }

            else -> super.resolveToSingleError(ex, env)
        }
    }

}
