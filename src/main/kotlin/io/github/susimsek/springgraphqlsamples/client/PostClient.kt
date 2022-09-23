package io.github.susimsek.springgraphqlsamples.client

import io.github.susimsek.springgraphqlsamples.graphql.type.PostPayload
import org.springframework.graphql.client.HttpGraphQlClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class PostClient(builder: HttpGraphQlClient.Builder<*>) {

    private lateinit var graphQlClient: HttpGraphQlClient

    init {
        this.graphQlClient = builder
            .url("http://localhost:9091/graphql")
            .build()
    }

    fun getPost(postId: String): Mono<PostPayload> {
        return graphQlClient
            .documentName("postQuery")
            .variable("id", postId)
            .retrieve("post")
            .toEntity(PostPayload::class.java)
    }
}
