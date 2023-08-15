package io.github.susimsek.springgraphqlsamples.graphql.controller

import com.ninjasquad.springmockk.MockkBean
import io.github.susimsek.springgraphqlsamples.exception.POST_NOT_FOUND_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.ResourceNotFoundException
import io.github.susimsek.springgraphqlsamples.graphql.GraphQlUnitTest
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.OrderType
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.PostOrderField
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.PostStatus
import io.github.susimsek.springgraphqlsamples.graphql.type.PagedEntityModel
import io.github.susimsek.springgraphqlsamples.graphql.type.PostPayload
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import io.github.susimsek.springgraphqlsamples.service.PostService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.graphql.execution.ErrorType
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.security.test.context.support.WithMockUser
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private const val DEFAULT_ID = "2e50aab8-cc23-4658-9305-49044a2cb8d3"
private const val DEFAULT_TITLE = "test"
private const val DEFAULT_CONTENT = "test content"

private const val UPDATED_TITLE = "updated test"
private val DEFAULT_STATUS = PostStatus.DRAFT
private const val DEFAULT_CREATED_DATE = "2023-01-21T22:40:12.710+03:00"

val DEFAULT_POST = PostPayload(
    id = DEFAULT_ID,
    title = DEFAULT_TITLE,
    content = DEFAULT_CONTENT,
    status = DEFAULT_STATUS,
    createdAt = OffsetDateTime.parse(DEFAULT_CREATED_DATE, DateTimeFormatter.ISO_DATE_TIME)
)

@OptIn(ExperimentalCoroutinesApi::class)
@WithMockUser(authorities = ["ROLE_USER"])
@GraphQlUnitTest([PostController::class])
class PostControllerTest {

    @Autowired
    private lateinit var graphQlTester: GraphQlTester

    @MockkBean
    private lateinit var postService: PostService

    private lateinit var post: PostPayload

    private lateinit var author: UserPayload

    @BeforeEach
    fun setUp() {
        post = PostPayload(
            id = DEFAULT_ID,
            title = DEFAULT_TITLE,
            content = DEFAULT_CONTENT,
            status = DEFAULT_STATUS,
            createdAt = OffsetDateTime.parse(DEFAULT_CREATED_DATE, DateTimeFormatter.ISO_DATE_TIME)
        )
        author = DEFAULT_USER
    }

    @Test
    fun `get all posts`() = runTest {
        val pageable = PageRequest.of(0, 1)
        val posts = listOf(post)
        val pagedData = PageImpl(posts, pageable, posts.size.toLong())
        coEvery { postService.getPosts(any()) } returns PagedEntityModel(pagedData)
        graphQlTester.documentName("postsQuery")
            .variable("page", 0)
            .variable("size", 1)
            .variable(
                "orders",
                mapOf(
                    "field" to PostOrderField.createdAt,
                    "order" to OrderType.DESC
                )
            )
            .execute()
            .path("data.posts.pageInfo.totalCount").entity(Int::class.java).isEqualTo(posts.size)
            .path("data.posts.content.[*]").entityList(Any::class.java).hasSize(1)
            .path("data.posts.content.[*]").entityList(Any::class.java).hasSize(1)
            .path("data.posts.content[0].id").entity(String::class.java).isEqualTo(DEFAULT_ID)
            .path("data.posts.content.[0].title").entity(String::class.java).isEqualTo(
                DEFAULT_TITLE.replaceFirstChar {
                    if (it.isLowerCase()) {
                        it.titlecase(Locale.getDefault())
                    } else {
                        it.toString()
                    }
                }
            )

        coVerify(exactly = 1) { postService.getPosts(any()) }
    }

    @Test
    fun `search posts`() = runTest {
        coEvery { postService.searchPosts(any(), any()) } returns flowOf(post)
        graphQlTester.documentName("searchPostsQuery")
            .variable("page", 0)
            .variable("size", 1)
            .variable("searchPhrase", "test")
            .execute()
            .path("data.searchPosts[*]").entityList(Any::class.java).hasSize(1)
            .path("data.searchPosts[0].id").entity(String::class.java).isEqualTo(DEFAULT_ID)
            .path("data.searchPosts[0].title").entity(String::class.java).isEqualTo(
                DEFAULT_TITLE.replaceFirstChar {
                    if (it.isLowerCase()) {
                        it.titlecase(Locale.getDefault())
                    } else {
                        it.toString()
                    }
                }
            )

        coVerify(exactly = 1) { postService.searchPosts(any(), any()) }
    }

    @Test
    fun `get post by id`() = runTest {
        coEvery { postService.getPost(any()) } returns post
        val id = post.id

        graphQlTester
            .documentName("postQuery")
            .variable("id", id)
            .execute()
            .path("data.post.id").entity(String::class.java).isEqualTo(DEFAULT_ID)
            .path("data.post.title").entity(String::class.java).isEqualTo(
                DEFAULT_TITLE.replaceFirstChar {
                    if (it.isLowerCase()) {
                        it.titlecase(Locale.getDefault())
                    } else {
                        it.toString()
                    }
                }
            )
            .path("data.post.content").entity(String::class.java).isEqualTo(DEFAULT_CONTENT.lowercase())

        coVerify(exactly = 1) { postService.getPost(any()) }
    }

    @Test
    fun `get post with author`() = runTest {
        coEvery { postService.getPost(any()) } returns post
        coEvery { postService.getPostsWithAuthors(any()) } returns mapOf(post to author)

        val inputPlaceHolder = "\$id"
        // language=GraphQL
        val document = """
        query Post($inputPlaceHolder: ID!) {
            post(id: $inputPlaceHolder) {
                id
                author { 
                  id
                  username
                }
            }
        }        
        """.trim()

        graphQlTester
            .document(document)
            .variable("id", DEFAULT_ID)
            .execute()
            .path("data.post.id").entity(String::class.java).isEqualTo(DEFAULT_ID)
            .path("data.post.author.id").entity(String::class.java).isEqualTo(author.id!!)
            .path("data.post.author.username").entity(String::class.java).isEqualTo(author.username!!)

        coVerify(exactly = 1) { postService.getPost(any()) }
        coVerify(exactly = 1) { postService.getPostsWithAuthors(any()) }
    }

    @Test
    fun `get post by id when not found`() = runTest {
        coEvery { postService.getPost(any()) } throws ResourceNotFoundException(
            POST_NOT_FOUND_MSG_CODE,
            arrayOf(DEFAULT_ID)
        )

        val inputPlaceHolder = "\$id"
        // language=GraphQL
        val document = """
        query Post($inputPlaceHolder: ID!) {
            post(id: $inputPlaceHolder) {
                id
                title
                content
            }
        }        
        """.trim()
        graphQlTester.document(document)
            .variable("id", DEFAULT_ID)
            .execute()
            .errors()
            .satisfy { errors ->
                assertThat(errors).hasSize(1)
                assertThat(errors[0].errorType).isEqualTo(ErrorType.NOT_FOUND)
            }
        coVerify(exactly = 1) { postService.getPost(any()) }
    }

    @Test
    fun `create post`() = runTest {
        coEvery { postService.createPost(any(), any()) } returns post

        val input = mapOf(
            "title" to DEFAULT_TITLE,
            "content" to DEFAULT_CONTENT
        )

        graphQlTester
            .documentName("createPostMutation")
            .variable("input", input)
            .execute()
            .path("data.createPost.id").entity(String::class.java).isEqualTo(DEFAULT_ID)
            .path("data.createPost.title").entity(String::class.java).isEqualTo(
                DEFAULT_TITLE.replaceFirstChar {
                    if (it.isLowerCase()) {
                        it.titlecase(Locale.getDefault())
                    } else {
                        it.toString()
                    }
                }
            )
        coVerify(exactly = 1) { postService.createPost(any(), any()) }
    }

    @Test
    fun `update post`() = runTest {
        post.title = UPDATED_TITLE
        coEvery { postService.updatePost(any()) } returns post

        val input = mapOf(
            "id" to DEFAULT_ID,
            "title" to UPDATED_TITLE,
            "content" to DEFAULT_CONTENT
        )

        graphQlTester
            .documentName("updatePostMutation")
            .variable("input", input)
            .execute()
            .path("data.updatePost.id").entity(String::class.java).isEqualTo(DEFAULT_ID)
            .path("data.updatePost.title").entity(String::class.java).isEqualTo(
                UPDATED_TITLE.replaceFirstChar {
                    if (it.isLowerCase()) {
                        it.titlecase(Locale.getDefault())
                    } else {
                        it.toString()
                    }
                }
            )
        coVerify(exactly = 1) { postService.updatePost(any()) }
    }

    @Test
    fun `delete post`() = runTest {
        coEvery { postService.deletePost(any()) } returns DEFAULT_ID

        graphQlTester
            .documentName("deletePostMutation")
            .variable("id", DEFAULT_ID)
            .execute()
            .path("data.deletePost").entity(String::class.java).isEqualTo(DEFAULT_ID)
        coVerify(exactly = 1) { postService.deletePost(any()) }
    }

    @Test
    fun `post added subscription`() = runTest {
        post.title = DEFAULT_TITLE.replaceFirstChar {
            if (it.isLowerCase()) {
                it.titlecase(Locale.getDefault())
            } else {
                it.toString()
            }
        }
        every { postService.postAdded() } returns flowOf(post)

        graphQlTester
            .documentName("postAddedSubscription")
            .executeSubscription()
            .toFlux("postAdded", PostPayload::class.java)
            .`as`(StepVerifier::create)
            .expectNext(post)
            .expectNextCount(0)
            .verifyComplete()

        verify(exactly = 1) { postService.postAdded() }
    }
}
