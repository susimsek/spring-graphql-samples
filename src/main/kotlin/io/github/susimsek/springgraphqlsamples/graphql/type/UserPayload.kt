package io.github.susimsek.springgraphqlsamples.graphql.type

import java.time.OffsetDateTime
import java.util.Locale

data class UserPayload(
    val id: String? = null,

    var username: String? = null,

    var firstName: String? = null,

    var lastName: String? = null,

    var email: String? = null,

    var createdAt: OffsetDateTime,

    var lang: Locale
)
