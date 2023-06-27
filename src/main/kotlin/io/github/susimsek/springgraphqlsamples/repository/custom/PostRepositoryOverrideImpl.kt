package io.github.susimsek.springgraphqlsamples.repository.custom

import io.github.susimsek.springgraphqlsamples.domain.Post
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query

class PostRepositoryOverrideImpl(
    private val mongoTemplate: ReactiveMongoTemplate
) : PostRepositoryOverride {

    override suspend fun findAll(pageable: Pageable): Page<Post> {
        val query = Query()
        return mongoTemplate.find(Query.of(query).with(pageable), Post::class.java)
            .collectList()
            .zipWith(
                mongoTemplate.count(query, Post::class.java)
            )
            .map { PageImpl(it.t1, pageable, it.t2) }
            .awaitSingle()
    }

    /*
    override suspend fun findAllByPosition(
        limit: Int,
        scrollPosition: ScrollPosition,
        sort: Sort
    ): Window<Post> {
        val query = Query()
            .limit(limit)
            .with(scrollPosition)
            .with(sort)

        return mongoTemplate.scroll(query, Post::class.java)
            .awaitSingle()
    }

     */
}
