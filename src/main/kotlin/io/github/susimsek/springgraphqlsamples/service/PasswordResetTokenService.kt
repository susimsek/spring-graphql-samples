package io.github.susimsek.springgraphqlsamples.service

import io.github.susimsek.springgraphqlsamples.domain.PasswordResetToken
import io.github.susimsek.springgraphqlsamples.repository.PasswordResetTokenRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class PasswordResetTokenService(
    private val passwordResetTokenRepository: PasswordResetTokenRepository
) {

    suspend fun createToken(userId: String, token: String): PasswordResetToken {
        val passwordResetToken = PasswordResetToken(userId)
        passwordResetToken.token = token
        passwordResetToken.expiryDate = OffsetDateTime.now().plusMinutes(60 * 24)
        passwordResetTokenRepository.save(passwordResetToken)
        return passwordResetToken
    }

    suspend fun findByToken(token: String): PasswordResetToken? {
        return passwordResetTokenRepository.findByToken(token)
    }

    suspend fun delete(token: PasswordResetToken) {
        passwordResetTokenRepository.delete(token)
    }

    suspend fun verifyExpiration(token: PasswordResetToken): Boolean {
        return token.expiryDate.isAfter(OffsetDateTime.now())
    }
}
