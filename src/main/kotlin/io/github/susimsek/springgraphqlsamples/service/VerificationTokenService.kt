package io.github.susimsek.springgraphqlsamples.service

import io.github.susimsek.springgraphqlsamples.domain.VerificationToken
import io.github.susimsek.springgraphqlsamples.repository.VerificationTokenRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class VerificationTokenService(
    private val verificationTokenRepository: VerificationTokenRepository
) {

    suspend fun createToken(userId: String, token: String): VerificationToken {
        val verificationToken = VerificationToken(userId)
        verificationToken.token = token
        verificationToken.expiryDate = OffsetDateTime.now().plusMinutes(60 * 24)
        verificationTokenRepository.save(verificationToken)
        return verificationToken
    }

    suspend fun findByToken(token: String): VerificationToken? {
        return verificationTokenRepository.findByToken(token)
    }

    suspend fun delete(token: VerificationToken) {
        verificationTokenRepository.delete(token)
    }

    suspend fun verifyExpiration(token: VerificationToken): Boolean {
        return token.expiryDate.isAfter(OffsetDateTime.now())
    }
}
