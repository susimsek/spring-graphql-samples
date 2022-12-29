package io.github.susimsek.springgraphqlsamples.exception

open class ValidationException(message: String?, args: Array<Any>? = null) : GraphqlException(message, args)
