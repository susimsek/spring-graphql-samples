package io.github.susimsek.springgraphqlsamples.service

import io.github.susimsek.springgraphqlsamples.domain.RefreshToken
import io.github.susimsek.springgraphqlsamples.exception.INVALID_REFRESH_TOKEN_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.InvalidTokenException
import io.github.susimsek.springgraphqlsamples.repository.RefreshTokenRepository
import io.github.susimsek.springgraphqlsamples.security.jwt.Token
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    suspend fun createToken(userId: String, token: Token): RefreshToken {
        val refreshToken = RefreshToken(userId)
        refreshToken.token = token.token
        refreshToken.expiryDate = OffsetDateTime.now().plusSeconds(token.expiresIn)
        refreshTokenRepository.save(refreshToken)
        return refreshToken
    }

    suspend fun deleteByUserId(userId: String) {
        refreshTokenRepository.deleteByUserId(userId)
    }

    suspend fun findByToken(token: String): RefreshToken? {
        return refreshTokenRepository.findByToken(token)
    }

    suspend fun verifyExpiration(token: RefreshToken): Boolean {
        val isExpired = !token.expiryDate.isAfter(OffsetDateTime.now())
        if (isExpired) {
            refreshTokenRepository.delete(token)
            throw InvalidTokenException(INVALID_REFRESH_TOKEN_MSG_CODE)
        }
        return true
    }
}
