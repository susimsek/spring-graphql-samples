package io.github.susimsek.springgraphqlsamples.graphql.controller

import io.github.susimsek.springgraphqlsamples.graphql.input.LoginInput
import io.github.susimsek.springgraphqlsamples.graphql.type.Token
import io.github.susimsek.springgraphqlsamples.service.AuthenticationService
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

@Controller
class AuthenticationController(private val authenticationService: AuthenticationService) {

    @MutationMapping
    suspend fun login(@Argument @Valid input: LoginInput): Token {
        return authenticationService.authorize(input)
    }
}
