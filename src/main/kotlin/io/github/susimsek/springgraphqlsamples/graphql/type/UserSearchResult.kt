package io.github.susimsek.springgraphqlsamples.graphql.type

import org.springframework.data.domain.Page

class UserSearchResult(private val result: Page<UserPayload>) {
    val pageInfo: PageInfo
        get() = PageInfo(result)
    val users: List<UserPayload>
        get() = result.content
}