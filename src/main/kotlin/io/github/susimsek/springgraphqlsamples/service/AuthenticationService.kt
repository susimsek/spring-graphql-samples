package io.github.susimsek.springgraphqlsamples.service

import io.github.susimsek.springgraphqlsamples.graphql.input.LoginInput
import io.github.susimsek.springgraphqlsamples.graphql.type.Token
import io.github.susimsek.springgraphqlsamples.security.cipher.SecurityCipher
import io.github.susimsek.springgraphqlsamples.security.jwt.TokenProvider
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticationService(
    private val tokenProvider: TokenProvider,
    private val securityCipher: SecurityCipher,
    private val authenticationManager: ReactiveAuthenticationManager
) {
    suspend fun authorize(credentials: LoginInput): Token {
        return authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
            credentials.login, credentials.password
            )
        )
            .map {
                val token = tokenProvider.createToken(it)
                token.token = securityCipher.encrypt(token.token)
                token
            }.awaitSingle()
    }

    suspend fun logout(): Boolean {
        tokenProvider.deleteTokenCookie()
        return Mono.just(true)
            .awaitSingle()
    }
}
