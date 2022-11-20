package io.github.susimsek.springgraphqlsamples.exception

open class GraphqlException(message: String?, var args: Array<Any>? = null) : RuntimeException(message)
