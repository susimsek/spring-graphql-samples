package io.github.susimsek.springgraphqlsamples.repository

import io.github.susimsek.springgraphqlsamples.domain.Post
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface PostRepository : ReactiveMongoRepository<Post, String> {

    fun findByIdNotNull(pageable: Pageable): Flux<Post>
    fun findAllByCreatedByIn(createdBy: MutableSet<String>?): Flux<Post>
}
