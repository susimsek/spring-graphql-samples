package io.github.susimsek.springgraphqlsamples.repository

import io.github.susimsek.springgraphqlsamples.domain.VerificationToken
import org.springframework.stereotype.Repository

@Repository
interface VerificationTokenRepository : TokenRepository<VerificationToken>
