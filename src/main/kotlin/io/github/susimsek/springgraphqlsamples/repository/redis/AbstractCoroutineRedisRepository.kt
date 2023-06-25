package io.github.susimsek.springgraphqlsamples.repository.redis

import io.github.susimsek.springgraphqlsamples.config.AppProperties
import io.github.susimsek.springgraphqlsamples.config.Cache
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveHashOperations
import org.springframework.data.redis.core.ReactiveRedisOperations
import reactor.core.publisher.Mono
import java.time.Duration

abstract class AbstractCoroutineRedisRepository<T : Any> : CoroutineRedisRepository<T> {

    private lateinit var key: String

    @Autowired
    private lateinit var redisOperations: ReactiveRedisOperations<String, Any>

    @Autowired
    private lateinit var appProperties: AppProperties

    private lateinit var reactiveHashOperations: ReactiveHashOperations<String, String, T>
    private lateinit var cacheProperties: Cache

    abstract fun initKey(): String

    @PostConstruct
    @Suppress("UnusedPrivateMember")
    private fun init() {
        reactiveHashOperations = redisOperations.opsForHash()
        cacheProperties = appProperties.cache
        key = initKey()
    }

    override suspend fun save(id: String, entity: T): T {
        val duration = Duration.ofSeconds(cacheProperties.redis.timeToLiveSeconds)
        return put(key, id, entity, duration).map { entity }
            .awaitSingle()
    }

    override suspend fun findById(id: String): T? {
        return reactiveHashOperations[key, id].awaitSingleOrNull()
    }

    override suspend fun delete(id: String) {
        reactiveHashOperations.remove(key, id).awaitSingle()
    }

    override suspend fun deleteAll() {
        reactiveHashOperations.delete(key).awaitSingle()
    }

    private fun put(key: String, hashKey: String, entity: T, expiration: Duration): Mono<T> {
        val update = reactiveHashOperations.put(key, hashKey, entity)
        val setTtl = redisOperations.expire(key, expiration)
        return update.then(setTtl).map { entity }
    }
}
