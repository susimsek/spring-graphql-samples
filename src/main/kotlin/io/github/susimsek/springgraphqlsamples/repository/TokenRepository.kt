package io.github.susimsek.springgraphqlsamples.repository

import io.github.susimsek.springgraphqlsamples.domain.Token
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

@NoRepositoryBean
interface TokenRepository<T : Token> :
    CoroutineCrudRepository<T, String>,
    CoroutineSortingRepository<T, String> {
    suspend fun findByToken(token: String): T?
}
