package io.github.susimsek.springgraphqlsamples.repository

import io.github.susimsek.springgraphqlsamples.domain.PasswordResetToken
import org.springframework.stereotype.Repository

@Repository
interface PasswordResetTokenRepository : TokenRepository<PasswordResetToken>
