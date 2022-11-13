package io.github.susimsek.springgraphqlsamples.graphql.controller

import io.github.susimsek.springgraphqlsamples.graphql.input.LoginInput
import io.github.susimsek.springgraphqlsamples.graphql.type.Token
import io.github.susimsek.springgraphqlsamples.service.AuthenticationService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*

@Controller
class AuthenticationController(private val authenticationService: AuthenticationService) {

    private val log = LoggerFactory.getLogger(javaClass)

    @MutationMapping
    suspend fun login(@Argument input: LoginInput, locale: Locale): Token {
        log.info("locale: {}", locale.toLanguageTag())
        return authenticationService.authorize(input)
    }

    @PostMapping("/login")
    @ResponseBody
    suspend fun loginRest(@RequestBody @Valid input: LoginInput, locale: Locale): Token {
        log.info(locale.toLanguageTag())
        return authenticationService.authorize(input)
    }
}
