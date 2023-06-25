package io.github.susimsek.springgraphqlsamples.repository.redis

interface CoroutineRedisRepository<T> {
    suspend fun save(id: String, entity: T): T
    suspend fun findById(id: String): T?
    suspend fun delete(id: String)
    suspend fun deleteAll()
}
