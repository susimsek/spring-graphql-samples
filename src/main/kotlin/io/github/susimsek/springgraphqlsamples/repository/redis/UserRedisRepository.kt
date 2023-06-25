package io.github.susimsek.springgraphqlsamples.repository.redis

import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import org.springframework.stereotype.Repository

@Repository
class UserRedisRepository : AbstractCoroutineRedisRepository<UserPayload>() {

    override fun initKey(): String {
        return "user"
    }
}
