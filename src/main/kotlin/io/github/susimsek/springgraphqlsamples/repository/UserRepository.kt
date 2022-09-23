package io.github.susimsek.springgraphqlsamples.repository

import io.github.susimsek.springgraphqlsamples.domain.User
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.graphql.data.GraphQlRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@GraphQlRepository
interface UserRepository : ReactiveCrudRepository<User, String>,
    ReactiveQuerydslPredicateExecutor<User>, UserRepositoryOverride {
    fun findOneByEmailIgnoreCase(email: String?): Mono<User>

    fun findOneByUsername(login: String): Mono<User>

    override fun count(): Mono<Long>

    fun findAllByIdIn(id: MutableSet<String>?): Flux<User>
}
