package io.github.susimsek.springgraphqlsamples.rest.controller

import io.github.susimsek.springgraphqlsamples.exception.INVALID_REFRESH_TOKEN_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.InvalidTokenException
import io.github.susimsek.springgraphqlsamples.exception.model.Problem
import io.github.susimsek.springgraphqlsamples.graphql.input.LoginInput
import io.github.susimsek.springgraphqlsamples.graphql.type.TokenPayload
import io.github.susimsek.springgraphqlsamples.rest.payload.LoginRequest
import io.github.susimsek.springgraphqlsamples.rest.payload.LogoutPayload
import io.github.susimsek.springgraphqlsamples.rest.payload.RefreshTokenRequest
import io.github.susimsek.springgraphqlsamples.security.jwt.Token
import io.github.susimsek.springgraphqlsamples.security.jwt.TokenProvider
import io.github.susimsek.springgraphqlsamples.security.recaptcha.RecaptchaService
import io.github.susimsek.springgraphqlsamples.service.AuthenticationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import java.security.Principal
import java.util.*

@Tag(name = "authentication", description = "Authentication API")
@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationRestController(
    private val authenticationService: AuthenticationService,
    private val recaptchaService: RecaptchaService,
    private val tokenProvider: TokenProvider
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Login")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = TokenPayload::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Problem::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Problem::class)
                    )
                ]
            )
        ]
    )
    @PostMapping("/login")
    suspend fun login(
        @RequestBody @Valid credentials: LoginRequest,
        @RequestHeader(required = false) recaptcha: String?,
        exchange: ServerWebExchange
    ): ResponseEntity<TokenPayload> {
        recaptchaService.validateToken(recaptcha)
        val payload = authenticationService.authorize(
            LoginInput(credentials.login, credentials.password)
        )
        val accessTokenCookie = tokenProvider.createAccessTokenCookie(
            Token(payload.accessToken, payload.accessTokenExpiresIn)
        )
        val refreshTokenCookie = tokenProvider.createRefreshTokenCookie(
            Token(payload.refreshToken, payload.refreshTokenExpiresIn)
        )

        exchange.response.addCookie(accessTokenCookie)
        exchange.response.addCookie(refreshTokenCookie)
        return ResponseEntity.ok()
            .body(payload)
    }

    @PostMapping("/logout")
    suspend fun logout(
        @AuthenticationPrincipal principal: Principal?,
        exchange: ServerWebExchange
    ): ResponseEntity<LogoutPayload> {
        if (principal != null) {
            log.info("user {} logged out", principal.name)
        }
        authenticationService.logout()
        val accessTokenCookie = tokenProvider.deleteAccessTokenCookie()
        val refreshTokenCookie = tokenProvider.deleteRefreshTokenCookie()

        exchange.response.addCookie(accessTokenCookie)
        exchange.response.addCookie(refreshTokenCookie)
        return ResponseEntity.ok()
            .body(LogoutPayload(success = true))
    }

    @PostMapping("/refresh-token")
    suspend fun refreshToken(
        @RequestBody(required = false) refreshTokenRequest: RefreshTokenRequest?,
        @CookieValue(required = false) refreshToken: String?,
        exchange: ServerWebExchange
    ): ResponseEntity<TokenPayload> {
        val token = when (refreshToken?.isNotBlank()) {
            true -> refreshToken
            else -> refreshTokenRequest?.refreshToken
        } ?: throw InvalidTokenException(INVALID_REFRESH_TOKEN_MSG_CODE)
        val payload = authenticationService.refreshToken(token)
        val accessTokenCookie = tokenProvider.createAccessTokenCookie(
            Token(payload.accessToken, payload.accessTokenExpiresIn)
        )
        val refreshTokenCookie = tokenProvider.createRefreshTokenCookie(
            Token(payload.refreshToken, payload.refreshTokenExpiresIn)
        )

        exchange.response.addCookie(accessTokenCookie)
        exchange.response.addCookie(refreshTokenCookie)
        return ResponseEntity.ok()
            .body(payload)
    }
}
