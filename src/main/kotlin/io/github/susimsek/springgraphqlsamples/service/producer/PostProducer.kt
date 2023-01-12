package io.github.susimsek.springgraphqlsamples.service.producer

import io.github.susimsek.springgraphqlsamples.graphql.type.PostPayload
import org.springframework.stereotype.Component
import reactor.core.publisher.Sinks

@Component
class PostProducer(private val postSink: Sinks.Many<PostPayload>) {

    suspend fun produce(event: PostPayload) {
        postSink.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST)
    }
}