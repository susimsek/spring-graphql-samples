package io.github.susimsek.springgraphqlsamples.graphql.controller

import com.ninjasquad.springmockk.MockkBean
import io.github.susimsek.springgraphqlsamples.config.GraphqlConfig
import io.github.susimsek.springgraphqlsamples.config.ValidationConfig
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.PostStatus
import io.github.susimsek.springgraphqlsamples.graphql.type.PostPayload
import io.github.susimsek.springgraphqlsamples.security.USER
import io.github.susimsek.springgraphqlsamples.service.PostService
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.security.test.context.support.WithMockUser
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
@Import(ValidationConfig::class, GraphqlConfig::class)
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
}
