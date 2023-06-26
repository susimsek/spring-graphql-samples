package io.github.susimsek.springgraphqlsamples.util

import org.springframework.http.ResponseCookie

object CookieUtil {

    fun createHttpOnlyCookie(
        name: String,
        value: String,
        maxAge: Long,
        domain: String
    ): ResponseCookie {
        return ResponseCookie.from(name, value)
            .maxAge(maxAge)
            .httpOnly(true)
            .path("/")
            .domain(domain)
            .build()
    }

    fun deleteHttpOnlyCookie(name: String, domain: String): ResponseCookie {
        return ResponseCookie.from(name, "")
            .maxAge(0)
            .httpOnly(true)
            .path("/")
            .domain(domain)
            .build()
    }
}
