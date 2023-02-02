package io.github.susimsek.springgraphqlsamples.graphql.type

import org.springframework.data.domain.Page

class PagedEntityModel<T>(private val result: Page<T>) {
    val pageInfo: PageInfo
        get() = PageInfo(result)
    val content: List<T>
        get() = result.content
}
