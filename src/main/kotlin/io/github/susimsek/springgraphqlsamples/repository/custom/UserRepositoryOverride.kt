package io.github.susimsek.springgraphqlsamples.repository.custom

import io.github.susimsek.springgraphqlsamples.domain.User
import io.github.susimsek.springgraphqlsamples.graphql.input.UserFilter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

fun interface UserRepositoryOverride {
    suspend fun findAllByFilter(filter: UserFilter?, pageable: Pageable): Page<User>
}
