package io.github.susimsek.springgraphqlsamples.service

import io.github.susimsek.springgraphqlsamples.exception.POST_NOT_FOUND_MSG_CODE
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.context.MessageSource
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
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

        val post = postRepository.save(entity)
        val payload = postMapper.toType(post)

        val event = payload
        val localizedTitle = messageSource.getMessage("post.title", arrayOf(event.title), locale)
        event.title = localizedTitle
        event.locale = locale

        flow.emit(event)
        // sink.emitNext(payload, Sinks.EmitFailureHandler.FAIL_FAST)
        return payload
    }

    suspend fun updatePost(input: UpdatePostInput): PostPayload {
        val currentUserId = getCurrentUserLogin().awaitSingleOrNull()
            ?: throw UsernameNotFoundException("User was not found")
        var post = postRepository.findById(input.id) ?: throw ResourceNotFoundException(
            POST_NOT_FOUND_MSG_CODE, arrayOf(input.id))
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
            POST_NOT_FOUND_MSG_CODE, arrayOf(id))
        if (post.createdBy != currentUserId) {
            throw AccessDeniedException(
                "User with id $currentUserId not authorized to access this post ${post.id}"
            )
        }
        postRepository.delete(post)
        return post.id
    }

    fun getPosts(pageRequest: Pageable): Flow<PostPayload> {
        return postRepository.findByIdNotNull(pageRequest)
            .map(postMapper::toType)
    }

    suspend fun getPost(id: String): PostPayload {
        val post = postRepository.findById(id) ?: throw ResourceNotFoundException(
            POST_NOT_FOUND_MSG_CODE, arrayOf(id))
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

    fun postAdded(): SharedFlow<PostPayload> {
        // return sink.asFlux()
        return flow.asSharedFlow()
    }
}
