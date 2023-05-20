package io.github.susimsek.springgraphqlsamples.service

import io.github.susimsek.springgraphqlsamples.exception.POST_NOT_FOUND_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.ResourceNotFoundException
import io.github.susimsek.springgraphqlsamples.graphql.DEFAULT_SIZE
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.PostStatus
import io.github.susimsek.springgraphqlsamples.graphql.input.AddPostInput
import io.github.susimsek.springgraphqlsamples.graphql.input.UpdatePostInput
import io.github.susimsek.springgraphqlsamples.graphql.type.PagedEntityModel
import io.github.susimsek.springgraphqlsamples.graphql.type.PostPayload
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import io.github.susimsek.springgraphqlsamples.repository.PostRepository
import io.github.susimsek.springgraphqlsamples.security.getCurrentUserLogin
import io.github.susimsek.springgraphqlsamples.service.mapper.PostMapper
import io.github.susimsek.springgraphqlsamples.service.pubsub.PostPubSubService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.context.MessageSource
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.ScrollPosition
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Window
import org.springframework.data.mongodb.core.query.TextCriteria
import org.springframework.graphql.data.query.ScrollSubrange
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class PostService(
    private val postRepository: PostRepository,
    private val postMapper: PostMapper,
    private val userService: UserService,
    private val messageSource: MessageSource,
    private val postPubSubService: PostPubSubService
) {

    suspend fun createPost(input: AddPostInput, locale: Locale): PostPayload {
        val entity = postMapper.toEntity(input)
        entity.status = PostStatus.DRAFT

        //  .doOnNext{ flow.emitNext(it, Sinks.EmitFailureHandler.FAIL_FAST)}

        val post = postRepository.save(entity)
        val payload = postMapper.toType(post)

        val message = payload
        val localizedTitle = messageSource.getMessage("post.title", arrayOf(message.title), locale)
        message.title = localizedTitle
        message.locale = locale

        postPubSubService.publish(message)
        // sink.emitNext(payload, Sinks.EmitFailureHandler.FAIL_FAST)
        return payload
    }

    suspend fun updatePost(input: UpdatePostInput): PostPayload {
        val currentUserId = getCurrentUserLogin().awaitSingleOrNull()
            ?: throw UsernameNotFoundException("User was not found")
        var post = postRepository.findById(input.id) ?: throw ResourceNotFoundException(
            POST_NOT_FOUND_MSG_CODE,
            arrayOf(input.id)
        )
        if (post.createdBy != currentUserId) {
            throw AccessDeniedException(
                "User with id $currentUserId not authorized to access this post ${post.id}"
            )
        }
        postMapper.partialUpdate(post, input)
        post = postRepository.save(post)
        return postMapper.toType(post)
    }

    suspend fun deletePost(id: String): String {
        val currentUserId = getCurrentUserLogin().awaitSingleOrNull()
            ?: throw UsernameNotFoundException("User was not found")
        val post = postRepository.findById(id) ?: throw ResourceNotFoundException(
            POST_NOT_FOUND_MSG_CODE,
            arrayOf(id)
        )
        if (post.createdBy != currentUserId) {
            throw AccessDeniedException(
                "User with id $currentUserId not authorized to access this post ${post.id}"
            )
        }
        postRepository.delete(post)
        return post.id
    }

    suspend fun getPosts(pageRequest: Pageable): PagedEntityModel<PostPayload> {
        val result = postRepository.findAll(pageRequest)
            .map(postMapper::toType)
        return PagedEntityModel<PostPayload>(result)
    }

    suspend fun getPost(id: String): PostPayload {
        val post = postRepository.findById(id) ?: throw ResourceNotFoundException(
            POST_NOT_FOUND_MSG_CODE,
            arrayOf(id)
        )
        return postMapper.toType(post)
    }

    suspend fun getPostsWithAuthors(posts: List<PostPayload>): Map<PostPayload, UserPayload?> {
        val authorIds = posts.map { post -> post.createdBy!! }.toMutableSet()
        val authors = userService.getUserByIdIn(authorIds).toList()
        val mappedUsers = mutableMapOf<PostPayload, UserPayload?>()
        posts.forEach { post ->
            mappedUsers[post] = authors.firstOrNull { it.id == post.createdBy }
        }
        return mappedUsers
    }

    fun getPostsByCreatedByIn(userIds: MutableSet<String>?): Flow<PostPayload> {
        return postRepository.findAllByCreatedByIn(userIds)
            .map(postMapper::toType)
    }

    suspend fun getUsersWithPosts(users: List<UserPayload>): Map<UserPayload, List<PostPayload>> {
        val userIds = users.map { user -> user.id!! }.toMutableSet()
        val posts = getPostsByCreatedByIn(userIds).toList()
        val mappedPosts = mutableMapOf<UserPayload, List<PostPayload>>()
        users.forEach { user ->
            mappedPosts[user] = posts.filter { it.createdBy == user.id }
        }
        return mappedPosts
    }

    fun postAdded(): Flow<PostPayload> {
        // return sink.asFlux()
        return postPubSubService.subscribe()
    }

    fun searchPosts(pageRequest: Pageable, searchPhrase: String): Flow<PostPayload> {
        val criteria = TextCriteria
            .forDefaultLanguage()
            .matchingPhrase(searchPhrase)
        return postRepository.findBy(criteria, pageRequest)
            .map(postMapper::toType)
    }

    suspend fun getPostsWithCursorPagination(
        subrange: ScrollSubrange,
        sort: Sort
    ): Window<PostPayload> {
        val limit = subrange.count().orElse(DEFAULT_SIZE)
        val position = subrange.position().orElse(ScrollPosition.offset())
        return postRepository.findAllByPosition(limit, position, sort)
            .map(postMapper::toType)
    }
}
