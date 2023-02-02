package io.github.susimsek.springgraphqlsamples.graphql.controller

import com.ninjasquad.springmockk.MockkBean
import io.github.susimsek.springgraphqlsamples.config.GraphqlConfig
import io.github.susimsek.springgraphqlsamples.config.ValidationConfig
import io.github.susimsek.springgraphqlsamples.exception.InvalidCaptchaException
import io.github.susimsek.springgraphqlsamples.exception.RECAPTCHA_INVALID_MSG_CODE
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.OrderType
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.UserOrderField
import io.github.susimsek.springgraphqlsamples.graphql.type.PagedEntityModel
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import io.github.susimsek.springgraphqlsamples.security.recaptcha.RecaptchaService
import io.github.susimsek.springgraphqlsamples.service.UserService
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.graphql.ExecutionGraphQlService
import org.springframework.graphql.execution.ErrorType
import org.springframework.graphql.test.tester.ExecutionGraphQlServiceTester
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.security.test.context.support.WithMockUser
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private const val DEFAULT_ID = "2e50aab8-cc23-4658-9305-49044a2cb8d3"
private const val DEFAULT_NAME = "john doe"
private const val DEFAULT_USERNAME = "johndoe"
private const val DEFAULT_PASSWORD = "passjohndoe"
private const val DEFAULT_FIRST_NAME = "john"
private const val DEFAULT_LAST_NAME = "doe"
private const val DEFAULT_EMAIL = "johndoe@localhost"
private val DEFAULT_LANG = Locale.ENGLISH
private const val DEFAULT_CREATED_DATE = "2023-01-21T22:40:12.710+03:00"

val DEFAULT_USER = UserPayload(
    id = DEFAULT_ID,
    username = DEFAULT_USERNAME,
    firstName = DEFAULT_FIRST_NAME,
    lastName = DEFAULT_LAST_NAME,
    email = DEFAULT_EMAIL,
    createdAt = OffsetDateTime.parse(DEFAULT_CREATED_DATE, DateTimeFormatter.ISO_DATE_TIME),
    lang = DEFAULT_LANG
)

@OptIn(ExperimentalCoroutinesApi::class)
@WithMockUser(authorities = ["ROLE_USER"])
@GraphQlTest(controllers = [UserController::class])
@Import(
    ValidationConfig::class,
    GraphqlConfig::class,
    MessageSourceAutoConfiguration::class
)
class UserControllerTest {

    private lateinit var graphQlTester: GraphQlTester

    @MockkBean
    private lateinit var userService: UserService

    @MockkBean
    private lateinit var recaptchaService: RecaptchaService

    private lateinit var user: UserPayload

    @BeforeEach
    fun setUp(@Autowired delegateService: ExecutionGraphQlService) {
        user = DEFAULT_USER

        val graphQlService = ExecutionGraphQlService { request ->
            request.configureExecutionInput { _, builder ->
                builder.graphQLContext(mapOf("recaptcha" to RECAPTCHA_RESPONSE)).build()
            }
            delegateService.execute(request)
        }

        graphQlTester = ExecutionGraphQlServiceTester.create(graphQlService)
    }

    @Test
    fun me() = runTest {
        coEvery { userService.getCurrentUser() } returns user

        graphQlTester
            .documentName("meQuery")
            .execute()
            .path("data.me.id").entity(String::class.java).isEqualTo(DEFAULT_ID)
            .path("data.me.username").entity(String::class.java).isEqualTo(DEFAULT_USERNAME)
            .path("data.me.email").entity(String::class.java).isEqualTo(DEFAULT_EMAIL)

        coVerify(exactly = 1) { userService.getCurrentUser() }
    }

    @Test
    fun `me with name`() = runTest {
        coEvery { userService.getCurrentUser() } returns user
        coEvery { userService.getName(any()) } returns DEFAULT_NAME
        // language=GraphQL
        val document = """
            query Me {
                me{
                    id
                    name
                }
            }
        """.trim()

        graphQlTester
            .document(document)
            .execute()
            .path("data.me.id").entity(String::class.java).isEqualTo(DEFAULT_ID)
            .path("data.me.name").entity(String::class.java).isEqualTo(DEFAULT_NAME)

        coVerify(exactly = 1) { userService.getCurrentUser() }
        coVerify(exactly = 1) { userService.getName(any()) }
    }

    @Test
    fun `get all users`() = runTest {
        val pageable = PageRequest.of(0, 1)
        val users = listOf(user)
        val pagedData = PageImpl(users, pageable, users.size.toLong())
        coEvery { userService.getUsers(any(), any()) } returns PagedEntityModel(pagedData)

        graphQlTester.documentName("usersQuery")
            .variable("page", pageable.pageNumber)
            .variable("size", pageable.pageSize)
            .variable(
                "orders",
                mapOf(
                    "field" to UserOrderField.createdAt,
                    "order" to OrderType.DESC
                )
            )
            .execute()
            .path("data.users.pageInfo.totalCount").entity(Int::class.java).isEqualTo(users.size)
            .path("data.users.content.[*]").entityList(Any::class.java).hasSize(1)
            .path("data.users.content.[0].id").entity(String::class.java).isEqualTo(DEFAULT_ID)
            .path("data.users.content.[0].username").entity(String::class.java).isEqualTo(DEFAULT_USERNAME)

        coVerify(exactly = 1) { userService.getUsers(any(), any()) }
    }

    @Test
    fun `create user`() = runTest {
        coEvery { userService.createUser(any()) } returns user
        coEvery { recaptchaService.validateToken(any()) } returns true

        val input = mapOf(
            "username" to DEFAULT_USERNAME,
            "password" to DEFAULT_PASSWORD,
            "firstName" to DEFAULT_FIRST_NAME,
            "lastName" to DEFAULT_LAST_NAME,
            "email" to DEFAULT_EMAIL,
            "lang" to DEFAULT_LANG
        )

        graphQlTester
            .documentName("createUserMutation")
            .variable("input", input)
            .execute()
            .path("data.createUser.id").entity(String::class.java).isEqualTo(DEFAULT_ID)
            .path("data.createUser.username").entity(String::class.java).isEqualTo(DEFAULT_USERNAME)
            .path("data.createUser.email").entity(String::class.java).isEqualTo(DEFAULT_EMAIL)

        coVerify(exactly = 1) { userService.createUser(any()) }
        coVerify(exactly = 1) { recaptchaService.validateToken(any()) }
    }

    @Test
    fun `create user when recaptcha token is invalid`() = runTest {
        coEvery { recaptchaService.validateToken(any()) } throws InvalidCaptchaException(RECAPTCHA_INVALID_MSG_CODE)

        val input = mapOf(
            "username" to DEFAULT_USERNAME,
            "password" to DEFAULT_PASSWORD,
            "firstName" to DEFAULT_FIRST_NAME,
            "lastName" to DEFAULT_LAST_NAME,
            "email" to DEFAULT_EMAIL,
            "lang" to DEFAULT_LANG
        )

        graphQlTester
            .documentName("createUserMutation")
            .variable("input", input)
            .execute()
            .errors()
            .satisfy { errors ->
                Assertions.assertThat(errors).hasSize(1)
                Assertions.assertThat(errors[0].errorType).isEqualTo(ErrorType.BAD_REQUEST)
            }

        coVerify(exactly = 0) { userService.createUser(any()) }
        coVerify(exactly = 1) { recaptchaService.validateToken(any()) }
    }
}
