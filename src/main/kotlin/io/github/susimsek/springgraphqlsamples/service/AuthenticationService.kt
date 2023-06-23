package io.github.susimsek.springgraphqlsamples.service

import io.github.susimsek.springgraphqlsamples.exception.INVALID_REFRESH_TOKEN_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.InvalidTokenException
import io.github.susimsek.springgraphqlsamples.graphql.input.LoginInput
import io.github.susimsek.springgraphqlsamples.graphql.type.TokenPayload
import io.github.susimsek.springgraphqlsamples.security.UserContextProvider
import io.github.susimsek.springgraphqlsamples.security.cipher.SecurityCipher
import io.github.susimsek.springgraphqlsamples.security.getCurrentUserLogin
import io.github.susimsek.springgraphqlsamples.security.jwt.TokenProvider
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticationService(
    private val tokenProvider: TokenProvider,
    private val securityCipher: SecurityCipher,
    private val authenticationManager: ReactiveAuthenticationManager,
    private val refreshTokenService: RefreshTokenService,
    private val userContextProvider: UserContextProvider
) {
    suspend fun authorize(credentials: LoginInput): TokenPayload {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                credentials.login,
                credentials.password
            )
        ).awaitSingle()
        val userDetails = authentication.principal as UserDetails
        val accessToken = tokenProvider.createAccessToken(userDetails)
        val refreshToken = tokenProvider.createRefreshToken(authentication)
        refreshTokenService.createToken(authentication.name, refreshToken)
        return TokenPayload(
            accessToken = securityCipher.encrypt(accessToken.token),
            refreshToken = refreshToken.token,
            accessTokenExpiresIn = accessToken.expiresIn,
            refreshTokenExpiresIn = refreshToken.expiresIn
        )
    }

    suspend fun logout(): Boolean {
        val currentUserId = getCurrentUserLogin().awaitSingleOrNull()
        if (currentUserId !== null) {
            refreshTokenService.deleteByUserId(currentUserId)
        }
        tokenProvider.deleteAccessTokenCookie()
        tokenProvider.deleteRefreshTokenCookie()
        return Mono.just(true)
            .awaitSingle()
    }

    suspend fun refreshToken(token: String): TokenPayload {
        val refreshToken = refreshTokenService.findByToken(token)
            ?: throw InvalidTokenException(INVALID_REFRESH_TOKEN_MSG_CODE)
        refreshTokenService.verifyExpiration(refreshToken)
        val userDetails = userContextProvider.getUserDetails(refreshToken.userId)
        val accessToken = tokenProvider.createAccessToken(userDetails)
        val refreshTokenExpiresIn = tokenProvider.calculateRemainingExpiresIn(refreshToken.expiryDate)
        return TokenPayload(
            accessToken = securityCipher.encrypt(accessToken.token),
            refreshToken = refreshToken.token,
            accessTokenExpiresIn = accessToken.expiresIn,
            refreshTokenExpiresIn = refreshTokenExpiresIn
        )
    }
}
