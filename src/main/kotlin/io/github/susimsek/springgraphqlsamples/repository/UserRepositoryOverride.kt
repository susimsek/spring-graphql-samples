package io.github.susimsek.springgraphqlsamples.repository

import io.github.susimsek.springgraphqlsamples.domain.User
import io.github.susimsek.springgraphqlsamples.graphql.input.UserFilter
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux

interface UserRepositoryOverride {
    fun findAllByFilter(filter: UserFilter?, pageable: Pageable): Flux<User>
}
