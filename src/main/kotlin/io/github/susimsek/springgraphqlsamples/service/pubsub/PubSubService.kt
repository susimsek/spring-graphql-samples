package io.github.susimsek.springgraphqlsamples.service.pubsub

import kotlinx.coroutines.flow.Flow

interface PubSubService<T> {
    suspend fun publish(message: T)
    fun subscribe(): Flow<T>
}
