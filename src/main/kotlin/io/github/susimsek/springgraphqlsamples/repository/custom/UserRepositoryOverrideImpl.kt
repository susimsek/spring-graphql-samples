package io.github.susimsek.springgraphqlsamples.repository.custom

import io.github.susimsek.springgraphqlsamples.domain.User
import io.github.susimsek.springgraphqlsamples.graphql.input.UserFilter
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class UserRepositoryOverrideImpl(
    private val mongoTemplate: ReactiveMongoTemplate
) : UserRepositoryOverride {
    override suspend fun findAllByFilter(filter: UserFilter?, pageable: Pageable): Page<User> {
        val query = Query(
            Criteria.where("id").ne(null)
                .and("activated").`is`(true)
        )
        filter?.let {
            filter.toCriteria()?.let {
                query.addCriteria(it)
            }
        }

        return mongoTemplate.find(Query.of(query).with(pageable), User::class.java)
            .collectList()
            .zipWith(
                mongoTemplate.count(query, User::class.java)
            )
            .map { PageImpl(it.t1, pageable, it.t2) }
            .awaitSingle()
    }
}
