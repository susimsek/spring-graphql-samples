package io.github.susimsek.springgraphqlsamples.graphql.controller

import io.github.susimsek.springgraphqlsamples.client.PostClient
import io.github.susimsek.springgraphqlsamples.config.GraphqlDateTimeConfig
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.PostStatus
import io.github.susimsek.springgraphqlsamples.graphql.type.PostPayload
import io.github.susimsek.springgraphqlsamples.service.PostService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.security.test.context.support.WithMockUser
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private const val DEFAULT_ID = "632c8028feb9e053546a88f2"
private const val DEFAULT_TITLE = "test"
private const val DEFAULT_CONTENT = "test content"
private val DEFAULT_STATUS = PostStatus.DRAFT
private const val DEFAULT_CREATED_DATE = "2022-09-22T18:32:56+03:00"

@GraphQlTest(controllers = [PostController::class])
@Import(GraphqlDateTimeConfig::class)
@WithMockUser
class PostControllerTest {

    @Autowired
    private lateinit var graphQlTester: GraphQlTester

    @MockBean
    private lateinit var postService: PostService

    @MockBean
    private lateinit var postClient: PostClient

    private lateinit var post: PostPayload

    @BeforeEach
    fun initTest() {
        post = PostPayload(
            id = DEFAULT_ID,
            title = DEFAULT_TITLE,
            content = DEFAULT_CONTENT,
            status = DEFAULT_STATUS,
            createdDate = OffsetDateTime.parse(DEFAULT_CREATED_DATE, DateTimeFormatter.ISO_DATE_TIME)
        )
    }

    @Test
    fun post_shouldReturnPost() {
        Mockito.`when`(postService.getPost(Mockito.anyString())).thenReturn(Mono.just(post))

        val id = post.id

        graphQlTester
            .documentName("postQuery")
            .variable("id",  id)
            .execute()
            .path("data.post.id").entity(String::class.java).isEqualTo(DEFAULT_ID)
            .path("data.post.title").entity(String::class.java).isEqualTo(DEFAULT_TITLE)
            .path("data.post.content").entity(String::class.java).isEqualTo(DEFAULT_CONTENT)

        Mockito.verify(postService, Mockito.times(1)).getPost(Mockito.anyString())
    }
}