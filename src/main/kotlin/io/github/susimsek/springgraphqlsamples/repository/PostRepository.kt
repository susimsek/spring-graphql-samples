package io.github.susimsek.springgraphqlsamples.repository

import io.github.susimsek.springgraphqlsamples.domain.Post
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.query.TextCriteria
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.graphql.data.GraphQlRepository

@GraphQlRepository
interface PostRepository :
    CoroutineCrudRepository<Post, String>,
    // ReactiveQuerydslPredicateExecutor<Post>,
    CoroutineSortingRepository<Post, String> {

    fun findByIdNotNull(pageable: Pageable): Flow<Post>
    fun findAllByCreatedByIn(createdBy: MutableSet<String>?): Flow<Post>

    fun findBy(criteria: TextCriteria, pageable: Pageable): Flow<Post>
}
