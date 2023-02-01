package io.github.susimsek.springgraphqlsamples.graphql.controller

import com.ninjasquad.springmockk.MockkBean
import io.github.susimsek.springgraphqlsamples.config.GraphqlConfig
import io.github.susimsek.springgraphqlsamples.config.ValidationConfig
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import io.github.susimsek.springgraphqlsamples.security.recaptcha.RecaptchaService
import io.github.susimsek.springgraphqlsamples.service.UserService
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.graphql.ExecutionGraphQlService
import org.springframework.graphql.test.tester.ExecutionGraphQlServiceTester
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.security.test.context.support.WithMockUser
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*


private const val DEFAULT_ID = "2e50aab8-cc23-4658-9305-49044a2cb8d3"
private const val DEFAULT_USERNAME = "johndoe"
private const val DEFAULT_PASSWORD = "passjohndoe"
private const val DEFAULT_FIRST_NAME = "john"
private const val DEFAULT_LAST_NAME = "doe"
private const val DEFAULT_EMAIL = "johndoe@localhost"
private val DEFAULT_LANG = Locale.ENGLISH
private const val DEFAULT_CREATED_DATE = "2023-01-21T22:40:12.710+03:00"


@OptIn(ExperimentalCoroutinesApi::class)
@WithMockUser(authorities = ["ROLE_USER"])
@GraphQlTest(controllers = [UserController::class])
@Import(ValidationConfig::class, GraphqlConfig::class,
    MessageSourceAutoConfiguration::class)
class UserControllerTest {


    private lateinit var graphQlTester: GraphQlTester

    @MockkBean
    private lateinit var userService: UserService

    @MockkBean
    private lateinit var recaptchaService: RecaptchaService

    private lateinit var user: UserPayload

    @BeforeEach
    fun setUp(@Autowired delegateService: ExecutionGraphQlService) {
        user = UserPayload(
            id = DEFAULT_ID,
            username = DEFAULT_USERNAME,
            firstName = DEFAULT_FIRST_NAME,
            lastName = DEFAULT_LAST_NAME,
            email = DEFAULT_EMAIL,
            createdAt = OffsetDateTime.parse(DEFAULT_CREATED_DATE, DateTimeFormatter.ISO_DATE_TIME),
            lang = DEFAULT_LANG
        )

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
    fun `create user`() = runTest {
        coEvery { userService.createUser(any()) } returns user
        coEvery { recaptchaService.validateToken(any())} returns true

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
        coEvery { recaptchaService.validateToken(any())} returns true
    }
}