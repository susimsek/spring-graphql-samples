package io.github.susimsek.springgraphqlsamples.graphql.controller

import com.ninjasquad.springmockk.MockkBean
import io.github.susimsek.springgraphqlsamples.exception.InvalidCaptchaException
import io.github.susimsek.springgraphqlsamples.exception.RECAPTCHA_INVALID_MSG_CODE
import io.github.susimsek.springgraphqlsamples.graphql.WebFluxUnitTest
import io.github.susimsek.springgraphqlsamples.graphql.type.TokenPayload
import io.github.susimsek.springgraphqlsamples.rest.controller.AuthenticationRestController
import io.github.susimsek.springgraphqlsamples.rest.payload.LoginRequest
import io.github.susimsek.springgraphqlsamples.security.jwt.TokenProvider
import io.github.susimsek.springgraphqlsamples.security.recaptcha.RecaptchaService
import io.github.susimsek.springgraphqlsamples.service.AuthenticationService
import io.github.susimsek.springgraphqlsamples.util.CookieUtil
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

const val DEFAULT_COOKIE_DOMAIN = "localhost"
const val ACCESS_TOKEN_COOKIE_NAME = "accessToken"
const val ACCESS_TOKEN_COOKIE_MAX_AGE: Long = 86400
const val REFRESH_TOKEN_COOKIE_NAME = "refreshToken"
const val REFRESH_TOKEN_COOKIE_MAX_AGE: Long = 259200

val DEFAULT_ACCESS_TOKEN_COOKIE = CookieUtil.createHttpOnlyCookie(
    name = ACCESS_TOKEN_COOKIE_NAME,
    value = DEFAULT_ACCESS_TOKEN,
    domain = DEFAULT_COOKIE_DOMAIN,
    maxAge = ACCESS_TOKEN_COOKIE_MAX_AGE
)

val DELETED_ACCESS_TOKEN_COOKIE = CookieUtil.deleteHttpOnlyCookie(
    name = ACCESS_TOKEN_COOKIE_NAME,
    domain = DEFAULT_COOKIE_DOMAIN
)

val DEFAULT_REFRESH_TOKEN_COOKIE = CookieUtil.createHttpOnlyCookie(
    name = REFRESH_TOKEN_COOKIE_NAME,
    value = DEFAULT_REFRESH_TOKEN,
    domain = DEFAULT_COOKIE_DOMAIN,
    maxAge = REFRESH_TOKEN_COOKIE_MAX_AGE
)

val DELETED_REFRESH_TOKEN_COOKIE = CookieUtil.deleteHttpOnlyCookie(
    name = REFRESH_TOKEN_COOKIE_NAME,
    domain = DEFAULT_COOKIE_DOMAIN
)

private const val AUTH_API_URL = "/api/v1/auth"

@OptIn(ExperimentalCoroutinesApi::class)
@WebFluxUnitTest([AuthenticationRestController::class])
class AuthRestControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var authenticationService: AuthenticationService

    @MockkBean
    private lateinit var recaptchaService: RecaptchaService

    @MockkBean
    private lateinit var tokenProvider: TokenProvider

    @Test
    fun authorize() = runTest {
        coEvery { authenticationService.authorize(any()) } returns TokenPayload(
            DEFAULT_ACCESS_TOKEN,
            DEFAULT_REFRESH_TOKEN
        )
        coEvery { recaptchaService.validateToken(any()) } returns true

        every { tokenProvider.createAccessTokenCookie(any()) } returns DEFAULT_ACCESS_TOKEN_COOKIE
        every { tokenProvider.createRefreshTokenCookie(any()) } returns DEFAULT_REFRESH_TOKEN_COOKIE

        val req = LoginRequest(login = DEFAULT_USERNAME, password = DEFAULT_PASSWORD)

        webTestClient
            .post()
            .uri("$AUTH_API_URL/login")
            .body(BodyInserters.fromValue(req))
            .header("recaptcha", RECAPTCHA_RESPONSE)
            .exchange()
            .expectStatus().isOk
            .expectCookie().httpOnly(ACCESS_TOKEN_COOKIE_NAME, true)
            .expectCookie().httpOnly(REFRESH_TOKEN_COOKIE_NAME, true)
            .expectBody()
            .jsonPath("$.accessToken").isEqualTo(DEFAULT_ACCESS_TOKEN)
            .jsonPath("$.refreshToken").isEqualTo(DEFAULT_REFRESH_TOKEN)

        coVerify(exactly = 1) { authenticationService.authorize(any()) }
        coVerify(exactly = 1) { recaptchaService.validateToken(any()) }
        verify(exactly = 1) { tokenProvider.createAccessTokenCookie(any()) }
        verify(exactly = 1) { tokenProvider.createRefreshTokenCookie(any()) }
    }

    @Test
    fun `authorize fails`() = runTest {
        coEvery { authenticationService.authorize(any()) } throws BadCredentialsException("invalid credentials")
        coEvery { recaptchaService.validateToken(any()) } returns true

        val req = LoginRequest(login = DEFAULT_USERNAME, password = DEFAULT_PASSWORD)

        webTestClient
            .post()
            .uri("$AUTH_API_URL/login")
            .body(BodyInserters.fromValue(req))
            .header("recaptcha", RECAPTCHA_RESPONSE)
            .exchange()
            .expectStatus().isUnauthorized
            .expectCookie().doesNotExist(ACCESS_TOKEN_COOKIE_NAME)
            .expectCookie().doesNotExist(REFRESH_TOKEN_COOKIE_NAME)
            .expectBody()
            .jsonPath("$.accessToken").doesNotExist()
            .jsonPath("$.refreshToken").doesNotExist()

        coVerify(exactly = 1) { authenticationService.authorize(any()) }
        coVerify(exactly = 1) { recaptchaService.validateToken(any()) }
    }

    @Test
    fun `authorize when recaptcha token is invalid`() = runTest {
        coEvery { recaptchaService.validateToken(any()) } throws InvalidCaptchaException(RECAPTCHA_INVALID_MSG_CODE)

        val req = LoginRequest(login = DEFAULT_USERNAME, password = DEFAULT_PASSWORD)

        webTestClient
            .post()
            .uri("$AUTH_API_URL/login")
            .body(BodyInserters.fromValue(req))
            .header("recaptcha", RECAPTCHA_RESPONSE)
            .exchange()
            .expectStatus().isBadRequest
            .expectCookie().doesNotExist(ACCESS_TOKEN_COOKIE_NAME)
            .expectCookie().doesNotExist(REFRESH_TOKEN_COOKIE_NAME)
            .expectBody()
            .jsonPath("$.accessToken").doesNotExist()
            .jsonPath("$.refreshToken").doesNotExist()

        coVerify(exactly = 0) { authenticationService.authorize(any()) }
        coVerify(exactly = 1) { recaptchaService.validateToken(any()) }
    }

    @Test
    @WithMockUser(authorities = ["ROLE_USER"])
    fun logout() = runTest {
        coEvery { authenticationService.logout() } returns true

        every { tokenProvider.deleteAccessTokenCookie() } returns DELETED_ACCESS_TOKEN_COOKIE
        every { tokenProvider.deleteRefreshTokenCookie() } returns DELETED_REFRESH_TOKEN_COOKIE

        webTestClient
            .post()
            .uri("$AUTH_API_URL/logout")
            .exchange()
            .expectStatus().isOk
            .expectCookie().httpOnly(ACCESS_TOKEN_COOKIE_NAME, true)
            .expectCookie().httpOnly(REFRESH_TOKEN_COOKIE_NAME, true)
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)

        coVerify(exactly = 1) { authenticationService.logout() }
        verify(exactly = 1) { tokenProvider.deleteAccessTokenCookie() }
        verify(exactly = 1) { tokenProvider.deleteRefreshTokenCookie() }
    }
}
