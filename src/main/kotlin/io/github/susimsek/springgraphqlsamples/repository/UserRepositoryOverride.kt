package io.github.susimsek.springgraphqlsamples.repository

import io.github.susimsek.springgraphqlsamples.domain.User
import io.github.susimsek.springgraphqlsamples.graphql.input.UserFilter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserRepositoryOverride {
    fun findAllByFilter(filter: UserFilter?, pageable: Pageable): Mono<Page<User>>
}
