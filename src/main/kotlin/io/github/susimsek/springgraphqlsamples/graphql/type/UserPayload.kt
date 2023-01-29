package io.github.susimsek.springgraphqlsamples.graphql.type

import io.github.susimsek.springgraphqlsamples.domain.Role
import java.time.OffsetDateTime
import java.util.*

data class UserPayload(
    val id: String? = null,

    var username: String? = null,

    var firstName: String? = null,

    var lastName: String? = null,

    var email: String? = null,

    var createdAt: OffsetDateTime,

    var lang: Locale,

    var roles: MutableSet<Role> = mutableSetOf()
)
