package io.github.susimsek.springgraphqlsamples.rest.controller

import io.github.susimsek.springgraphqlsamples.graphql.input.LoginInput
import io.github.susimsek.springgraphqlsamples.graphql.type.TokenPayload
import io.github.susimsek.springgraphqlsamples.rest.payload.LoginRequest
import io.github.susimsek.springgraphqlsamples.rest.payload.LogoutPayload
import io.github.susimsek.springgraphqlsamples.security.jwt.TokenProvider
import io.github.susimsek.springgraphqlsamples.security.recaptcha.RecaptchaService
import io.github.susimsek.springgraphqlsamples.service.AuthenticationService
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationRestController(
    private val authenticationService: AuthenticationService,
    private val recaptchaService: RecaptchaService,
    private val tokenProvider: TokenProvider
) {

    @PostMapping("/login")
    suspend fun login(
        @RequestBody credentials: LoginRequest,
        @RequestHeader(required = false) recaptcha: String,
    ): ResponseEntity<TokenPayload> {
        recaptchaService.validateToken(recaptcha)
        val token = authenticationService.authorize(
            LoginInput(credentials.login, credentials.password)
        )
        val accessTokenCookie = tokenProvider.createAccessTokenCookie(token)
        val refreshTokenCookie = tokenProvider.createRefreshTokenCookie(token)
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(token)
    }

    @PostMapping("/logout")
    suspend fun logout(): ResponseEntity<LogoutPayload> {
        authenticationService.logout()
        val accessTokenCookie = tokenProvider.deleteAccessTokenCookie()
        val refreshTokenCookie = tokenProvider.deleteRefreshTokenCookie()
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(LogoutPayload(success = true))
    }
}
