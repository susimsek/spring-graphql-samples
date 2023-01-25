package io.github.susimsek.springgraphqlsamples.graphql.controller

import com.ninjasquad.springmockk.MockkBean
import io.github.susimsek.springgraphqlsamples.config.GraphqlConfig
import io.github.susimsek.springgraphqlsamples.config.ValidationConfig
import io.github.susimsek.springgraphqlsamples.exception.POST_NOT_FOUND_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.ResourceNotFoundException
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.PostStatus
import io.github.susimsek.springgraphqlsamples.graphql.type.PostPayload
import io.github.susimsek.springgraphqlsamples.security.USER
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
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.graphql.execution.ErrorType
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.security.test.context.support.WithMockUser
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private const val DEFAULT_ID = "632c8028feb9e053546a88f2"
private const val DEFAULT_TITLE = "test"
private const val DEFAULT_CONTENT = "test content"
private val DEFAULT_STATUS = PostStatus.DRAFT
private const val DEFAULT_CREATED_DATE = "2023-01-21T22:40:12.710+03:00"

@OptIn(ExperimentalCoroutinesApi::class)
@WithMockUser(authorities = [USER])
@GraphQlTest(controllers = [PostController::class])
@Import(ValidationConfig::class, GraphqlConfig::class, MessageSourceAutoConfiguration::class)
class PostControllerTest {

    @Autowired
    private lateinit var graphQlTester: GraphQlTester

    @MockkBean
    private lateinit var postService: PostService

    private lateinit var post: PostPayload

    @BeforeEach
    fun initTest() {
        post = PostPayload(
            id = DEFAULT_ID,
            title = DEFAULT_TITLE,
            content = DEFAULT_CONTENT,
            status = DEFAULT_STATUS,
            createdAt = OffsetDateTime.parse(DEFAULT_CREATED_DATE, DateTimeFormatter.ISO_DATE_TIME)
        )
    }

    @Test
    fun post_shouldReturnPost() = runTest {
        coEvery { postService.getPost(any()) } returns post
        val id = post.id

        graphQlTester
            .documentName("postQuery")
            .variable("id", id)
            .execute()
            .path("data.post.id").entity(String::class.java).isEqualTo(DEFAULT_ID)
            .path("data.post.title").entity(String::class.java).isEqualTo(DEFAULT_TITLE.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                else it.toString()})
            .path("data.post.content").entity(String::class.java).isEqualTo(DEFAULT_CONTENT.lowercase())

        coVerify(exactly = 1) { postService.getPost(any()) }
    }

    @Test
    fun post_whenNotFound() = runTest {
        coEvery {  postService.getPost(any()) } throws ResourceNotFoundException(
        POST_NOT_FOUND_MSG_CODE, arrayOf(DEFAULT_ID))

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
               assertThat(errors[0].errorType).isEqualTo(ErrorType.NOT_FOUND)}
        coVerify(exactly = 1) { postService.getPost(any()) }
    }

    @Test
    fun createPost_shouldReturnPost() = runTest {
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
            .path("data.createPost.title").entity(String::class.java).isEqualTo(DEFAULT_TITLE.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                else it.toString()})
        coVerify(exactly = 1) {  postService.createPost(any(), any()) }
    }

    @Test
    fun postAdded_shouldReceiveNewPost() = runTest {
        post.title = DEFAULT_TITLE.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
            else it.toString()}
        every { postService.postAdded() } returns flowOf(post)

        graphQlTester
            .documentName("postAddedSubscription")
            .executeSubscription()
            .toFlux("postAdded", PostPayload::class.java)
            .`as` (StepVerifier::create)
            .expectNext(post)
            .expectNextCount(0)
            .verifyComplete()

        verify (exactly = 1) {  postService.postAdded() }
    }
}
