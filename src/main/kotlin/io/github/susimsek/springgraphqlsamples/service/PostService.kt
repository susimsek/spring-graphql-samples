package io.github.susimsek.springgraphqlsamples.service

import io.github.susimsek.springgraphqlsamples.exception.ResourceNotFoundException
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.PostStatus
import io.github.susimsek.springgraphqlsamples.graphql.input.AddPostInput
import io.github.susimsek.springgraphqlsamples.graphql.input.UpdatePostInput
import io.github.susimsek.springgraphqlsamples.graphql.type.PostPayload
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import io.github.susimsek.springgraphqlsamples.repository.PostRepository
import io.github.susimsek.springgraphqlsamples.security.getCurrentUserLogin
import io.github.susimsek.springgraphqlsamples.service.mapper.PostMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.context.MessageSource
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class PostService(
    private val postRepository: PostRepository,
    private val postMapper: PostMapper,
    private val userService: UserService,
    private val messageSource: MessageSource
) {

    // private val sink = Sinks.many().replay().latest<PostPayload>()
    private val flow = MutableSharedFlow<PostPayload>(replay = 1)

    suspend fun createPost(input: AddPostInput, locale: Locale): PostPayload {
        val entity = postMapper.toEntity(input)
        entity.status = PostStatus.DRAFT

        //  .doOnNext{ flow.emitNext(it, Sinks.EmitFailureHandler.FAIL_FAST)}

        val payload = postRepository.save(entity)
            .map(postMapper::toType)
            .awaitSingle()

        val event = payload
        val localizedTitle = messageSource.getMessage("post.title", arrayOf(payload.title), locale)
        event.title = localizedTitle
        event.locale = locale

        flow.emit(event)
        // sink.emitNext(payload, Sinks.EmitFailureHandler.FAIL_FAST)
        return payload
    }

    suspend fun updatePost(input: UpdatePostInput): PostPayload {
        return getCurrentUserLogin()
            .flatMap { authUser ->
                postRepository.findById(input.id!!)
                    .flatMap {
                        if (it.createdBy == authUser) {
                            postMapper.partialUpdate(it, input)
                            postRepository.save(it)
                        } else {
                            Mono.error(
                                AccessDeniedException(
                                    "User with id $authUser not authorized to access this post ${it.id}"
                                )
                            )
                        }
                    }.switchIfEmpty(Mono.error((ResourceNotFoundException(
                        "error.post.not.found.message", arrayOf(input.id)))))
            }.map(postMapper::toType)
            .awaitSingle()
    }

    suspend fun deletePost(id: String): String {
        return getCurrentUserLogin()
            .flatMap { authUser ->
                postRepository.findById(id)
                    .flatMap {
                        if (it.createdBy == authUser) {
                            postRepository.delete(it)
                                .thenReturn(it)
                        } else {
                            Mono.error(
                                AccessDeniedException(
                                    "User with id $authUser not authorized to access this post ${it.id}"
                                )
                            )
                        }
                    }
                    .switchIfEmpty(Mono.error((ResourceNotFoundException(
                        "error.post.not.found.message", arrayOf(id)))))
            }.map { it.id }
            .awaitSingle()
    }

    fun getPosts(pageRequest: Pageable): Flow<PostPayload> {
        return postRepository.findByIdNotNull(pageRequest)
            .map(postMapper::toType)
            .asFlow()
    }

    suspend fun getPost(id: String): PostPayload {
        return postRepository.findById(id)
            .map(postMapper::toType)
            .switchIfEmpty(Mono.error((ResourceNotFoundException(
                "error.post.not.found.message", arrayOf(id)))))
            .awaitSingle()
    }

    fun getPostsWithAuthors(posts: MutableList<PostPayload>): Flow<UserPayload> {
        val authorIds = posts.map { post -> post.createdBy!! }.toMutableSet()
        val authors = userService.getUserByIdIn(authorIds)
        return Flux.fromIterable(posts)
            .flatMap { post ->
                authors.filter { post.createdBy.equals(it.id) }
            }.asFlow()
    }

    fun getPostsByCreatedByIn(userIds: MutableSet<String>?): Flux<PostPayload> {
        return postRepository.findAllByCreatedByIn(userIds)
            .map(postMapper::toType)
    }

    suspend fun getUsersWithPosts(users: MutableList<UserPayload>): Map<UserPayload, MutableList<PostPayload>> {
        val userIds = users.map { user -> user.id!! }.toMutableSet()
        return getPostsByCreatedByIn(userIds)
            .collectMultimap { it.createdBy!! }
            .map { m ->
                m.entries.associate { entry ->
                    users.find { it.id.equals(entry.key) }!! to
                            entry.value.toMutableList()
                }
            }.awaitSingle()
    }

    fun postAdded(): SharedFlow<PostPayload> {
        // return sink.asFlux()
        return flow.asSharedFlow()
    }
}
