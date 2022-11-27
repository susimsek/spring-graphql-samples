package io.github.susimsek.springgraphqlsamples.util

import java.util.Locale

fun String.capitalize(locale: Locale): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(locale)
        else it.toString()
    }
}