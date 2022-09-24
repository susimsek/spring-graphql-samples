package io.github.susimsek.springgraphqlsamples.client

import io.github.susimsek.springgraphqlsamples.graphql.type.PostPayload
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.graphql.client.HttpGraphQlClient
import org.springframework.stereotype.Component

@Component
class PostClient(builder: HttpGraphQlClient.Builder<*>) {

    private lateinit var graphQlClient: HttpGraphQlClient

    init {
        this.graphQlClient = builder
            .url("http://localhost:9091/graphql")
            .build()
    }

    suspend fun getPost(postId: String): PostPayload {
        return graphQlClient
            .documentName("postQuery")
            .variable("id", postId)
            .retrieve("post")
            .toEntity(PostPayload::class.java)
            .awaitSingle()
    }
}
