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
import org.reactivestreams.Publisher
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks

@Component
@PreAuthorize("isAuthenticated()")
class PostService(
    private val postRepository: PostRepository,
    private val postMapper: PostMapper,
    private val userService: UserService
) {

    private val sink = Sinks.many().replay().latest<PostPayload>()

    fun createPost(input: AddPostInput): Mono<PostPayload> {
        val entity = postMapper.toEntity(input)
        entity.status = PostStatus.DRAFT

        return postRepository.save(entity)
            .map(postMapper::toType)
            .doOnNext{ sink.emitNext(it, Sinks.EmitFailureHandler.FAIL_FAST)}
    }

    fun updatePost(input: UpdatePostInput): Mono<PostPayload> {
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
                    }.switchIfEmpty(Mono.error((ResourceNotFoundException("Post with id ${input.id} was not found"))))
            }.map(postMapper::toType)
    }

    fun deletePost(id: String): Mono<String> {
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
                    .switchIfEmpty(Mono.error((ResourceNotFoundException("Post with id $id was not found"))))
            }.map { it.id!! }
    }

    fun getPosts(pageRequest: Pageable): Flux<PostPayload> {
        return postRepository.findByIdNotNull(pageRequest)
            .map(postMapper::toType)
    }

    @PreAuthorize("permitAll()")
    fun getPost(id: String): Mono<PostPayload> {
        return postRepository.findById(id)
            .map(postMapper::toType)
            .switchIfEmpty(Mono.error((ResourceNotFoundException("Post with id $id was not found"))))
    }

    fun getPostsWithAuthors(posts: MutableList<PostPayload>): Flux<UserPayload> {
        val authorIds = posts.map { post -> post.createdBy!! }.toMutableSet()
        val authors = userService.getUserByIdIn(authorIds)
        return Flux.fromIterable(posts)
            .flatMap { post ->
                authors.filter { post.createdBy.equals(it.id) }
            }
    }

    fun getPostsByCreatedByIn(userIds: MutableSet<String>?): Flux<PostPayload> {
        return postRepository.findAllByCreatedByIn(userIds)
            .map(postMapper::toType)
    }

    fun getUsersWithPosts(users: MutableList<UserPayload>): Mono<Map<UserPayload, MutableList<PostPayload>>> {
        val userIds = users.map { user -> user.id!! }.toMutableSet()
        return getPostsByCreatedByIn(userIds)
            .collectMultimap { it.createdBy!! }
            .map { m ->
                m.entries.associate { entry ->
                    users.find { it.id.equals(entry.key) }!! to
                            entry.value.toMutableList()
                }
            }
    }

    @PreAuthorize("permitAll()")
    fun postAdded(): Publisher<PostPayload> {
        return sink.asFlux()
    }
}
