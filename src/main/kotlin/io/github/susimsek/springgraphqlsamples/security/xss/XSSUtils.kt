package io.github.susimsek.springgraphqlsamples.security.xss

import org.apache.commons.text.StringEscapeUtils

object XSSUtils {
    fun stripXSS(value: String?): String {
        val input = when (value.isNullOrBlank()) {
            true -> return ""
            else -> value
        }
        return StringEscapeUtils.escapeHtml4(input)
    }
}
