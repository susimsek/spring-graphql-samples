package io.github.susimsek.springgraphqlsamples.service.pubsub

import io.github.susimsek.springgraphqlsamples.graphql.type.PostPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks

@Service
class PostPubSubService(
    private val kafkaSink: Sinks.Many<PostPayload>,
    private val postFlow: MutableSharedFlow<PostPayload>
) : PubSubService<PostPayload> {
    override suspend fun publish(message: PostPayload) {
        kafkaSink.emitNext(message, Sinks.EmitFailureHandler.FAIL_FAST)
    }

    override fun subscribe(): Flow<PostPayload> {
        return postFlow.asSharedFlow()
    }
}
