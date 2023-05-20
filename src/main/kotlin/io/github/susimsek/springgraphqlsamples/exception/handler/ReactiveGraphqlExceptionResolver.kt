package io.github.susimsek.springgraphqlsamples.exception.handler

/*
@Component
class ReactiveGraphqlExceptionResolver(
    private val messageSource: MessageSource
) : DataFetcherExceptionResolverAdapter() {

    override fun resolveToMultipleErrors(
        ex: Throwable,
        env: DataFetchingEnvironment
    ): List<GraphQLError>? {
        return when (ex) {
            is ConstraintViolationException -> {
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

            else -> super.resolveToMultipleErrors(ex, env)
        }
    }

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
                GraphqlErrorBuilder.newError(env)
                    .message(responseError.message).errorType(errorType).build()
            }

            is ResourceNotFoundException -> {
                val errorMessage = messageSource.getMessage(ex.message!!, ex.args, locale)
                GraphqlErrorBuilder.newError(env)
                    .message(errorMessage).errorType(ErrorType.NOT_FOUND).build()
            }

            is ValidationException -> {
                val errorMessage = messageSource.getMessage(ex.message!!, ex.args, locale)
                badRequest(env, errorMessage)
            }

            is RateLimitingException -> {
                GraphqlErrorBuilder.newError(env)
                    .message("Throttled").errorType(ExtendedErrorType.THROTTLED).build()
            }

            else -> super.resolveToSingleError(ex, env)
        }
    }

    private fun badRequest(env: DataFetchingEnvironment, errorMessage: String): GraphQLError? {
        return GraphqlErrorBuilder.newError(env)
            .message(errorMessage).errorType(ErrorType.BAD_REQUEST).build()
    }
}

 */
