package io.github.susimsek.springgraphqlsamples.repository

import io.github.susimsek.springgraphqlsamples.domain.RefreshToken
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : TokenRepository<RefreshToken> {

    suspend fun deleteByUserId(userId: String): Long
}
