package io.github.susimsek.springgraphqlsamples.exception

class ValidationException(message: String?, args: Array<Any>? = null) : GraphqlException(message, args)
