package io.github.susimsek.springgraphqlsamples.util

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

@Suppress("TooGenericExceptionCaught")
inline fun <R> (() -> R).multiCatch(vararg exceptions: KClass<out Throwable>, thenDo: () -> R): R {
    return try {
        this()
    } catch (ex: Exception) {
        if (ex::class in exceptions) thenDo() else throw ex
    }
}
