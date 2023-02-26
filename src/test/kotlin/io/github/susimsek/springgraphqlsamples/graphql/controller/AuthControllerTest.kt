package io.github.susimsek.springgraphqlsamples.graphql.controller

import com.ninjasquad.springmockk.MockkBean
import io.github.susimsek.springgraphqlsamples.config.GraphqlConfig
import io.github.susimsek.springgraphqlsamples.config.ValidationConfig
import io.github.susimsek.springgraphqlsamples.exception.InvalidCaptchaException
import io.github.susimsek.springgraphqlsamples.exception.RECAPTCHA_INVALID_MSG_CODE
import io.github.susimsek.springgraphqlsamples.graphql.type.Token
import io.github.susimsek.springgraphqlsamples.security.recaptcha.RecaptchaService
import io.github.susimsek.springgraphqlsamples.service.AuthenticationService
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
import org.springframework.graphql.ExecutionGraphQlService
import org.springframework.graphql.execution.ErrorType
import org.springframework.graphql.execution.ReactiveSecurityDataFetcherExceptionResolver
import org.springframework.graphql.test.tester.ExecutionGraphQlServiceTester
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.security.authentication.BadCredentialsException

const val RECAPTCHA_RESPONSE = "03AFY_a8XSt-psckZobB96CoI6txaEmyt82-kBP" +
    "Vzsn6mbcDCKZro9TxmRF6kCRC5OznYTZfYUOl6CO9kqj2Y0dYN9jDk1JRHBBi9t7ZdVD5FKI" +
    "pbM1WYxUISCLf9hGjWz-BKhuLAWMPvhdGr3eO63NHGOzUk-w4Dig5-zNxtddHoKu7AT3DOObUp" +
    "Qe61to6LAiH6757Q-pQDR_rme_ApPrOpbytVe80zeGfJ2d1KmNsrLuZ1EpmXVpxOcJ2t675ZNvh1w" +
    "mXq8TS69Szt-qi07Bhk3RCJ7EX4V8Ao_UHo9MFR61msQhfdY0nlRecrpVTz5Hq5eqsNYfR1X5TZXo6h" +
    "G09fvHtPw3I2kO2LUYvt7K3RIFi6lzqa4ZQdyLObOyvwhHboSoH22WtMqTluPLz_Ln_D66yrmZuo3H" +
    "_OyuPYwBiN0lMdkZcHRDkKXXqJwhKg6g0OgE2dELt5Xl1al7bswKvghPgAxMaED8MYqllkvoBve3dO" +
    "YtlWgLUZz6pdETvWsO3fH0-6d7xb7"

private const val DEFAULT_USERNAME = "johndoe"
private const val DEFAULT_PASSWORD = "passjohndoe"

private const val DEFAULT_TOKEN = "pCtOnkH/FC5mYNhGRiJo3rwUqgj51trO7doM6gSHn/5hLQtNoORLwi" +
    "cqX7TPyPhyUCYpHlBVmmACXfePfzZMAkfUqnsnr9hEm3mY298dCgSgboDUihVHW71HKCRQfY+als2HiBsxY/M" +
    "oSGKUZgdg4908a+EC18wsQB/zO/uwgmzWSzIRUBoff3aPLtiJiDxQ2cV1kLMnpT+wuLpFcGGSpUMiSaHYF3/c" +
    "0oEcfJ7jyN5gC5aw1ux3nQd4Ulkuaed8SC6efAeCV3/oiLhsCUTYf6dy/QOmiqZyGC9JhQsbnWmSiJLsm0Zivz" +
    "0Y6yiA+aQUJ4nmwpeEdZUrA7yhKBzqm6Y1uAkmvgRLUll7SsvZflcQSUhEWMzgBdwd+B9i2UuKt+zSAFL6q2vUB1Y" +
    "5hePqCJKWXhLO67xoHJlj9QhLfjjPYZ7LU0DRc+jGf8M4lNypkAldz99JJOHn77LhqELb8aXwFM+y8f91px+WvLNHb" +
    "HnqNdx+QyseO1pez22zis4qr0mlvA6KyG68lyWE+T2WX5WV/pv2ryIZTjL2AIXiZhXssar48pfDFK81Ue+Kjwd3CQ" +
    "sQOIXrFHJOqJ+cDpxvS9FbNMqYyvdj/6FQ0IwiAiLukB2+LA8Vm1EieuJWWWEoWC28Z/4ck9hyUMAiAVS55nfeysES" +
    "PTbBB/m4XSKffK0jRnqUvUWIPCP4ymGL6etRoSg6cPHrV2a2+Kj7c7G3g5/xV+I4HVnMidCTbYg/ruY="

@OptIn(ExperimentalCoroutinesApi::class)
@GraphQlTest(controllers = [AuthenticationController::class])
@Import(
    ValidationConfig::class,
    GraphqlConfig::class,
    MessageSourceAutoConfiguration::class,
    ReactiveSecurityDataFetcherExceptionResolver::class
)
class AuthControllerTest {

    private lateinit var graphQlTester: GraphQlTester

    @MockkBean
    private lateinit var authenticationService: AuthenticationService

    @MockkBean
    private lateinit var recaptchaService: RecaptchaService

    @BeforeEach
    fun setUp(@Autowired delegateService: ExecutionGraphQlService) {
        val graphQlService = ExecutionGraphQlService { request ->
            request.configureExecutionInput { _, builder ->
                builder.graphQLContext(mapOf("recaptcha" to RECAPTCHA_RESPONSE)).build()
            }
            delegateService.execute(request)
        }
        graphQlTester = ExecutionGraphQlServiceTester.create(graphQlService)
    }

    @Test
    fun authorize() = runTest {
        coEvery { authenticationService.authorize(any()) } returns Token(DEFAULT_TOKEN)
        coEvery { recaptchaService.validateToken(any()) } returns true

        val input = mapOf(
            "login" to DEFAULT_USERNAME,
            "password" to DEFAULT_PASSWORD
        )

        graphQlTester
            .documentName("loginMutation")
            .variable("input", input)
            .execute()
            .path("data.login.token").entity(String::class.java).isEqualTo(DEFAULT_TOKEN)

        coVerify(exactly = 1) { authenticationService.authorize(any()) }
        coVerify(exactly = 1) { recaptchaService.validateToken(any()) }
    }

    @Test
    fun `authorize fails`() = runTest {
        coEvery { authenticationService.authorize(any()) } throws BadCredentialsException("invalid credentials")
        coEvery { recaptchaService.validateToken(any()) } returns true

        val input = mapOf(
            "login" to DEFAULT_USERNAME,
            "password" to DEFAULT_PASSWORD
        )

        graphQlTester
            .documentName("loginMutation")
            .variable("input", input)
            .execute()
            .errors()
            .satisfy { errors ->
                Assertions.assertThat(errors).hasSize(1)
                Assertions.assertThat(errors[0].errorType).isEqualTo(ErrorType.UNAUTHORIZED)
            }

        coVerify(exactly = 1) { authenticationService.authorize(any()) }
        coVerify(exactly = 1) { recaptchaService.validateToken(any()) }
    }

    @Test
    fun `authorize when recaptcha token is invalid`() = runTest {
        coEvery { recaptchaService.validateToken(any()) } throws InvalidCaptchaException(RECAPTCHA_INVALID_MSG_CODE)

        val input = mapOf(
            "login" to DEFAULT_USERNAME,
            "password" to DEFAULT_PASSWORD
        )

        graphQlTester
            .documentName("loginMutation")
            .variable("input", input)
            .execute()
            .errors()
            .satisfy { errors ->
                Assertions.assertThat(errors).hasSize(1)
                Assertions.assertThat(errors[0].errorType).isEqualTo(ErrorType.BAD_REQUEST)
            }

        coVerify(exactly = 0) { authenticationService.authorize(any()) }
        coVerify(exactly = 1) { recaptchaService.validateToken(any()) }
    }

    @Test
    fun logout() = runTest {
        coEvery { authenticationService.logout() } returns true

        graphQlTester
            .documentName("logoutMutation")
            .execute()
            .path("data.logout").entity(Boolean::class.java).isEqualTo(true)

        coVerify(exactly = 1) { authenticationService.logout() }
    }
}
