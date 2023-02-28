package io.github.susimsek.springgraphqlsamples.repository

import io.github.susimsek.springgraphqlsamples.domain.User
import io.github.susimsek.springgraphqlsamples.graphql.type.Token
import io.github.susimsek.springgraphqlsamples.repository.custom.UserRepositoryOverride
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.graphql.data.GraphQlRepository
import reactor.core.publisher.Mono

@GraphQlRepository
interface UserRepository :
    CoroutineCrudRepository<User, String>,
    CoroutineSortingRepository<User, String>,
    // ReactiveQuerydslPredicateExecutor<User>,
    UserRepositoryOverride {
    fun findOneByEmail(email: String): Mono<User>

    fun findOneByUsername(login: String): Mono<User>

    fun findAllByIdIn(id: MutableSet<String>?): Flow<User>

    suspend fun findByActivationToken(activationToken: String): User?
}
