package io.github.susimsek.springgraphqlsamples.exception

import org.springframework.security.core.AuthenticationException

class UserNotActivatedException(message: String, t: Throwable? = null) : AuthenticationException(message, t) 
