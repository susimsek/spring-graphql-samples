package io.github.susimsek.springgraphqlsamples.service

import io.github.susimsek.springgraphqlsamples.graphql.input.LoginInput
import io.github.susimsek.springgraphqlsamples.graphql.type.Token
import io.github.susimsek.springgraphqlsamples.security.cipher.SecurityCipher
import io.github.susimsek.springgraphqlsamples.security.jwt.TokenProvider
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AuthenticationService(
    private val tokenProvider: TokenProvider,
    private val securityCipher: SecurityCipher,
    private val authenticationManager: ReactiveAuthenticationManager
) {
    fun authorize(credentials: LoginInput): Mono<Token> {
        return Mono.just(credentials).flatMap { login ->
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(login.login, login.password))
                .map { securityCipher.encrypt(tokenProvider.createToken(it))!! }
        }
            .map { jwt ->
                Token(jwt)
            }
    }
}
