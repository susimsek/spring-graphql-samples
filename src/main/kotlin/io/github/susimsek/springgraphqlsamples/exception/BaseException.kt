package io.github.susimsek.springgraphqlsamples.exception

open class BaseException(message: String?, var args: Array<Any>? = null) : RuntimeException(message)
