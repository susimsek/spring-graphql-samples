package io.github.susimsek.springgraphqlsamples.exception

class ResourceNotFoundException(message: String?, args: Array<Any>? = null) : GraphqlException(message, args)
