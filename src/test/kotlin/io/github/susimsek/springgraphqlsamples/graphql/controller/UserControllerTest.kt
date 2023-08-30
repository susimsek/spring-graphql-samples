package io.github.susimsek.springgraphqlsamples.graphql.controller

import com.ninjasquad.springmockk.MockkBean
import io.github.susimsek.springgraphqlsamples.graphql.GraphQlUnitTest
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.OrderType
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.UserOrderField
import io.github.susimsek.springgraphqlsamples.graphql.type.PagedEntityModel
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import io.github.susimsek.springgraphqlsamples.service.UserService
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.graphql.ExecutionGraphQlService
import org.springframework.graphql.test.tester.ExecutionGraphQlServiceTester
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.security.test.context.support.WithMockUser
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private const val DEFAULT_ID = "2e50aab8-cc23-4658-9305-49044a2cb8d3"
private const val DEFAULT_NAME = "john doe"
private const val DEFAULT_FIRST_NAME = "john"
private const val DEFAULT_LAST_NAME = "doe"
private const val DEFAULT_EMAIL = "johndoe@localhost"
private val DEFAULT_LANG = Locale.ENGLISH
private const val DEFAULT_CREATED_DATE = "2023-01-21T22:40:12.710+03:00"
private const val DEFAULT_ACTIVATION_TOKEN = "a8813c7a-a04a-4bd9-943f-a11b8255f755"
private const val DEFAULT_RESET_TOKEN = "7b3fe2dc-0497-46d0-bd84-7f8e354385e9"

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
@GraphQlUnitTest([UserController::class])
class UserControllerTest {

    private lateinit var graphQlTester: GraphQlTester

    @MockkBean
    private lateinit var userService: UserService

    private lateinit var user: UserPayload

    @BeforeEach
    fun setUp(@Autowired delegateService: ExecutionGraphQlService) {
        user = DEFAULT_USER

        graphQlTester = ExecutionGraphQlServiceTester.builder(delegateService)
            .configureExecutionInput { _, builder ->
                builder.graphQLContext(mapOf("recaptcha" to RECAPTCHA_RESPONSE)).build()
            }.build()
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
    }

    @Test
    fun `activate account`() = runTest {
        coEvery { userService.activateAccount(any()) } returns true

        graphQlTester
            .documentName("activateAccountMutation")
            .variable("token", DEFAULT_ACTIVATION_TOKEN)
            .execute()
            .path("data.activateAccount").entity(Boolean::class.java).isEqualTo(true)

        coVerify(exactly = 1) { userService.activateAccount(any()) }
    }

    @Test
    fun `forgot password`() = runTest {
        coEvery { userService.forgotPassword(any()) } returns true

        graphQlTester
            .documentName("forgotPasswordMutation")
            .variable("email", DEFAULT_EMAIL)
            .execute()
            .path("data.forgotPassword").entity(Boolean::class.java).isEqualTo(true)

        coVerify(exactly = 1) { userService.forgotPassword(any()) }
    }

    @Test
    fun `forgot password with wrong email`() = runTest {
        coEvery { userService.forgotPassword(any()) } returns true

        graphQlTester
            .documentName("forgotPasswordMutation")
            .variable("email", "password-reset-wrong-email@example.com")
            .execute()
            .path("data.forgotPassword").entity(Boolean::class.java).isEqualTo(true)

        coVerify(exactly = 1) { userService.forgotPassword(any()) }
    }

    @Test
    fun `reset password`() = runTest {
        coEvery { userService.resetPassword(any(), any()) } returns true

        val input = mapOf(
            "token" to DEFAULT_RESET_TOKEN,
            "newPassword" to "new password"
        )

        graphQlTester
            .documentName("resetPasswordMutation")
            .variable("input", input)
            .execute()
            .path("data.resetPassword").entity(Boolean::class.java).isEqualTo(true)

        coVerify(exactly = 1) { userService.resetPassword(any(), any()) }
    }

    @Test
    fun `reset password with wrong token`() = runTest {
        coEvery { userService.resetPassword(any(), any()) } returns false

        val input = mapOf(
            "token" to "wrong reset token",
            "newPassword" to "new password"
        )

        graphQlTester
            .documentName("resetPasswordMutation")
            .variable("input", input)
            .execute()
            .path("data.resetPassword").entity(Boolean::class.java).isEqualTo(false)

        coVerify(exactly = 1) { userService.resetPassword(any(), any()) }
    }

    @Test
    fun `change password`() = runTest {
        coEvery { userService.changePassword(any(), any()) } returns true

        val input = mapOf(
            "currentPassword" to DEFAULT_PASSWORD,
            "newPassword" to "new password"
        )

        graphQlTester
            .documentName("changePasswordMutation")
            .variable("input", input)
            .execute()
            .path("data.changePassword").entity(Boolean::class.java).isEqualTo(true)

        coVerify(exactly = 1) { userService.changePassword(any(), any()) }
    }

    @Test
    fun `activate account with wrong token`() = runTest {
        coEvery { userService.activateAccount(any()) } returns false

        graphQlTester
            .documentName("activateAccountMutation")
            .variable("token", "wrong activation token")
            .execute()
            .path("data.activateAccount").entity(Boolean::class.java).isEqualTo(false)

        coVerify(exactly = 1) { userService.activateAccount(any()) }
    }
}
