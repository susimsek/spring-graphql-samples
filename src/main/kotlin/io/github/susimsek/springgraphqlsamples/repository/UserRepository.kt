package io.github.susimsek.springgraphqlsamples.repository

import io.github.susimsek.springgraphqlsamples.domain.User
import kotlinx.coroutines.flow.Flow
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.graphql.data.GraphQlRepository
import reactor.core.publisher.Mono

@GraphQlRepository
interface UserRepository :
    CoroutineCrudRepository<User, String>,
    CoroutineSortingRepository<User, String>,
    ReactiveQuerydslPredicateExecutor<User>,
    UserRepositoryOverride {
    fun findOneByEmailIgnoreCase(email: String?): Mono<User>

    fun findOneByUsername(login: String): Mono<User>

    override suspend fun count(): Long

    fun findAllByIdIn(id: MutableSet<String>?): Flow<User>
}
