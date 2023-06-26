package io.github.susimsek.springgraphqlsamples.util

import io.github.susimsek.springgraphqlsamples.security.jwt.Token
import org.springframework.http.ResponseCookie
import java.util.*
import kotlin.reflect.KClass

fun String.capitalize(locale: Locale): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) {
            it.titlecase(locale)
        } else {
            it.toString()
        }
    }
}

fun Token.httpOnlyCookie(name: String, domain: String): ResponseCookie {
    return CookieUtil.createHttpOnlyCookie(
        name,
        this.token,
        this.expiresIn,
        domain
    )
}

@Suppress("TooGenericExceptionCaught")
inline fun <R> (() -> R).multiCatch(vararg exceptions: KClass<out Throwable>, thenDo: () -> R): R {
    return try {
        this()
    } catch (ex: Exception) {
        if (ex::class in exceptions) thenDo() else throw ex
    }
}
