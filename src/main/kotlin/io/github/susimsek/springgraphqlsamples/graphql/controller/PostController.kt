package io.github.susimsek.springgraphqlsamples.graphql.controller

import io.github.susimsek.springgraphqlsamples.client.PostClient
import io.github.susimsek.springgraphqlsamples.graphql.DEFAULT_PAGE_NO
import io.github.susimsek.springgraphqlsamples.graphql.DEFAULT_SIZE
import io.github.susimsek.springgraphqlsamples.graphql.MAX_SIZE
import io.github.susimsek.springgraphqlsamples.graphql.input.AddPostInput
import io.github.susimsek.springgraphqlsamples.graphql.input.PostOrder
import io.github.susimsek.springgraphqlsamples.graphql.input.UpdatePostInput
import io.github.susimsek.springgraphqlsamples.graphql.type.PostPayload
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import io.github.susimsek.springgraphqlsamples.service.PostService
import kotlinx.coroutines.flow.toList
import org.reactivestreams.Publisher
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.BatchMapping
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SubscriptionMapping
import org.springframework.stereotype.Controller
import javax.validation.Valid

@Controller
class PostController(
    private val postService: PostService,
    private val postClient: PostClient
) {

    @MutationMapping
    suspend fun createPost(@Argument @Valid input: AddPostInput): PostPayload {
        return postService.createPost(input)
    }

    @MutationMapping
    suspend fun updatePost(@Argument @Valid input: UpdatePostInput): PostPayload {
        return postService.updatePost(input)
    }

    @MutationMapping
    suspend fun deletePost(@Argument id: String): String {
        return postService.deletePost(id)
    }

    @BatchMapping
    suspend fun posts(users: MutableList<UserPayload>): Map<UserPayload, MutableList<PostPayload>> {
        return postService.getUsersWithPosts(users)
    }

    @BatchMapping
    suspend fun author(posts: MutableList<PostPayload>): List<UserPayload> {
        return postService.getPostsWithAuthors(posts).toList()
    }

    @QueryMapping
    suspend fun posts(
        @Argument page: Int?,
        @Argument size: Int?,
        @Argument orders: MutableList<PostOrder>?
    ): List<PostPayload> {
        val pageNo = page ?: DEFAULT_PAGE_NO
        val sizeNo = (size ?: DEFAULT_SIZE).coerceAtMost(MAX_SIZE)
        val sort = orders?.map(PostOrder::toOrder)?.let { Sort.by(it) } ?: Sort.unsorted()
        val pageRequest = PageRequest.of(pageNo, sizeNo, sort)
        return postService.getPosts(pageRequest).toList()
    }

    @QueryMapping
    suspend fun post(@Argument id: String): PostPayload {
        return postService.getPost(id)
    }

    @QueryMapping
    suspend fun externalPost(@Argument id: String): PostPayload {
        return postClient.getPost(id)
    }

    @SubscriptionMapping
    fun postAdded(): Publisher<PostPayload> {
        return postService.postAdded()
    }
}
