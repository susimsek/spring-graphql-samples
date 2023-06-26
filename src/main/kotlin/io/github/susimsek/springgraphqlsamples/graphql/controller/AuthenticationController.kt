package io.github.susimsek.springgraphqlsamples.graphql.controller

import graphql.GraphQLContext
import io.github.susimsek.springgraphqlsamples.graphql.input.LoginInput
import io.github.susimsek.springgraphqlsamples.graphql.type.TokenPayload
import io.github.susimsek.springgraphqlsamples.security.recaptcha.RecaptchaService
import io.github.susimsek.springgraphqlsamples.service.AuthenticationService
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.ContextValue
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import java.security.Principal
import java.util.*

@Controller
class AuthenticationController(
    private val authenticationService: AuthenticationService,
    private val recaptchaService: RecaptchaService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @MutationMapping
    suspend fun login(
        @Argument input: LoginInput,
        @ContextValue(required = false) recaptcha: String?,
        locale: Locale,
        context: GraphQLContext
    ): TokenPayload {
        recaptchaService.validateToken(recaptcha)
        log.info("locale: {}", locale.toLanguageTag())
        val token = authenticationService.authorize(input)
        context.put("token", token)
        return token
    }

    @MutationMapping
    suspend fun logout(context: GraphQLContext, principal: Principal?): Boolean {
        if (principal != null) {
            log.info("user {} logged out", principal.name)
        }
        val result = authenticationService.logout()
        if (result) {
            context.put("token", TokenPayload())
        }
        return result
    }

    @MutationMapping
    suspend fun refreshToken(
        @Argument refreshToken: String,
        @ContextValue("refreshToken", required = false) refreshTokenCookie: String?,
        context: GraphQLContext
    ): TokenPayload {
        val token = when (refreshTokenCookie?.isNotBlank()) {
            true -> refreshTokenCookie
            else -> refreshToken
        }
        val payload = authenticationService.refreshToken(token)
        context.put("token", payload)
        return payload
    }
}
