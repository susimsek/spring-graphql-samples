package io.github.susimsek.springgraphqlsamples.exception

class InvalidCaptchaException(message: String?, args: Array<Any>? = null) : ValidationException(message, args)
