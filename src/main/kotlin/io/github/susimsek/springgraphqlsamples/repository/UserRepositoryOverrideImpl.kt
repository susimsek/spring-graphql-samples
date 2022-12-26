package io.github.susimsek.springgraphqlsamples.repository

import io.github.susimsek.springgraphqlsamples.domain.User
import io.github.susimsek.springgraphqlsamples.graphql.input.UserFilter
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class UserRepositoryOverrideImpl(
    private val mongoTemplate: ReactiveMongoTemplate
) : UserRepositoryOverride {
    override fun findAllByFilter(filter: UserFilter?, pageable: Pageable): Mono<Page<User>> {
        val query = Query(
            Criteria.where("id").ne(null)
                .and("activated").`is`(true)
        )
        query.with(pageable)
        filter?.let {
            filter.toCriteria()?.let {
                query.addCriteria(it)
            }
        }

        return mongoTemplate.find(query, User::class.java)
            .collectList()
            .zipWith(mongoTemplate.count(query, User::class.java))
            .map { PageImpl(it.t1, pageable, it.t2)  }
    }
}
