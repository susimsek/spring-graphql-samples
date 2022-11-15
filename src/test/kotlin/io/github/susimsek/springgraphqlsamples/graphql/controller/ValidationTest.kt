package io.github.susimsek.springgraphqlsamples.graphql.controller

import io.github.susimsek.springgraphqlsamples.config.GraphqlConfig
import io.github.susimsek.springgraphqlsamples.config.ValidationAutoConfiguration
import io.github.susimsek.springgraphqlsamples.exception.FieldError
import io.github.susimsek.springgraphqlsamples.service.PostService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.security.test.context.support.WithMockUser

private const val DEFAULT_CONTENT = "test content"

@OptIn(ExperimentalCoroutinesApi::class)
@GraphQlTest(controllers = [PostController::class])
@Import(*[GraphqlConfig::class, ValidationAutoConfiguration::class])
@WithMockUser
class ValidationTest {

    @Autowired
    private lateinit var graphQlTester: GraphQlTester

    @MockBean
    private lateinit var postService: PostService

    @Test
    fun createPost_shouldReturnLocalizedValidationMessageForInvalidInput() = runTest {
        graphQlTester
            .documentName("createPostMutation")
            .variable("input", hashMapOf(
                "title" to "",
                "content" to DEFAULT_CONTENT))
            .execute()
            .errors()
            .satisfy { errors ->
                val validationErrors = errors[0].extensions["errors"] as List<FieldError>
                assertThat(errors).hasSize(1)
                assertThat(errors[0].errorType).isEqualTo(graphql.ErrorType.ValidationError)
                assertThat(validationErrors.map { it.message }).contains("must not be blank")}
    }
}