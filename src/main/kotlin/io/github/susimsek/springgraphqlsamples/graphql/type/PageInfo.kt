package io.github.susimsek.springgraphqlsamples.graphql.type

import org.springframework.data.domain.Page

class PageInfo(private val result: Page<*>) {
    val pageNumber: Int
        get() = result.number
    val totalPages: Int
        get() = result.totalPages
    val totalCount: Long
        get() = result.totalElements
    val hasNext: Boolean
        get() = result.hasNext()
    val hasPrev: Boolean
        get() = result.hasPrevious()
    val nextPage: Int?
        get() = if (result.hasNext()) {
            result.number + 1
        } else null
    val prevPage: Int?
        get() = if (result.hasPrevious()) {
            result.number - 1
        } else null
}