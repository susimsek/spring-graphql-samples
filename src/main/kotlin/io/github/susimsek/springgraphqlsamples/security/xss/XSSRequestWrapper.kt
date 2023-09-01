package io.github.susimsek.springgraphqlsamples.security.xss

import io.github.susimsek.springgraphqlsamples.security.xss.XSSUtils.stripXSS
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

class XSSRequestWrapper(delegate: ServerHttpRequest) : ServerHttpRequestDecorator(delegate) {

    override fun getHeaders(): HttpHeaders {
        val headers = super.getHeaders()
        val newHeader = HttpHeaders()
        val mapHeaders = headers.toSingleValueMap()
        mapHeaders.forEach { (s1: String?, s2: String?) -> newHeader[s1] = listOf(stripXSS(s2)) }
        return newHeader
    }

    override fun getQueryParams(): MultiValueMap<String, String> {
        val queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(super.getQueryParams())
        queryParams.forEach { (s: String, s2: List<String?>) ->
            val updatedValues: MutableList<String> = ArrayList()
            for (value in s2) {
                updatedValues.add(stripXSS(value))
            }
            queryParams[s] = updatedValues
        }
        return queryParams
    }
}
