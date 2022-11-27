package io.github.susimsek.springgraphqlsamples.graphql.controller

import io.github.susimsek.springgraphqlsamples.graphql.DEFAULT_PAGE_NO
import io.github.susimsek.springgraphqlsamples.graphql.DEFAULT_SIZE
import io.github.susimsek.springgraphqlsamples.graphql.MAX_SIZE
import io.github.susimsek.springgraphqlsamples.graphql.input.AddPostInput
import io.github.susimsek.springgraphqlsamples.graphql.input.PostOrder
import io.github.susimsek.springgraphqlsamples.graphql.input.UpdatePostInput
import io.github.susimsek.springgraphqlsamples.graphql.type.PostPayload
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import io.github.susimsek.springgraphqlsamples.service.PostService
import jakarta.validation.Valid
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asPublisher
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.BatchMapping
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SubscriptionMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import java.util.Locale

@Controller
@PreAuthorize("isAuthenticated()")
class PostController(
    private val postService: PostService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @MutationMapping
    suspend fun createPost(@Argument @Valid input: AddPostInput, locale: Locale): PostPayload {
        return postService.createPost(input, locale)
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
    suspend fun posts(users: List<UserPayload>): Map<UserPayload, List<PostPayload>> {
        return postService.getUsersWithPosts(users)
    }

    @BatchMapping
    suspend fun author(posts: List<PostPayload>): Map<PostPayload, UserPayload?> {
        return postService.getPostsWithAuthors(posts)
    }

    @QueryMapping
    suspend fun posts(
        @Argument page: Int?,
        @Argument size: Int?,
        @Argument orders: MutableList<PostOrder>?,
        locale: Locale
    ): List<PostPayload> {
        log.info("called posts locale: {}", locale)
        val pageNo = page ?: DEFAULT_PAGE_NO
        val sizeNo = (size ?: DEFAULT_SIZE).coerceAtMost(MAX_SIZE)
        val sort = orders?.map(PostOrder::toOrder)?.let { Sort.by(it) } ?: Sort.unsorted()
        val pageRequest = PageRequest.of(pageNo, sizeNo, sort)
        return postService.getPosts(pageRequest).toList()
    }

    @QueryMapping
    @PreAuthorize("isAnonymous()")
    suspend fun post(@Argument id: String): PostPayload {
        return postService.getPost(id)
    }

    @SubscriptionMapping
    fun postAdded(locale: Locale): Publisher<PostPayload> {
        log.info("called postAdded locale: {}", locale)
        return postService.postAdded().asPublisher()
    }
}
