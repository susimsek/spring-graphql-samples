package io.github.susimsek.springgraphqlsamples.graphql.controller

import graphql.GraphQLContext
import io.github.susimsek.springgraphqlsamples.graphql.input.LoginInput
import io.github.susimsek.springgraphqlsamples.graphql.type.Token
import io.github.susimsek.springgraphqlsamples.service.AuthenticationService
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import java.util.Locale

@Controller
class AuthenticationController(private val authenticationService: AuthenticationService) {

    private val log = LoggerFactory.getLogger(javaClass)

    @MutationMapping
    suspend fun login(@Argument input: LoginInput, locale: Locale, context: GraphQLContext): Token {
        log.info("locale: {}", locale.toLanguageTag())
        val token = authenticationService.authorize(input)
        context.put("token", token)
        return token
    }
}
