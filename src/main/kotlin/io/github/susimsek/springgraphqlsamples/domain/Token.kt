package io.github.susimsek.springgraphqlsamples.domain

import java.time.Clock
import java.time.OffsetDateTime

open class Token(
    var token: String = "",
    var expiryDate: OffsetDateTime = OffsetDateTime.now(Clock.systemDefaultZone())
) : BaseEntity()
