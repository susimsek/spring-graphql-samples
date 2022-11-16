package io.github.susimsek.springgraphqlsamples.graphql.type

import io.github.susimsek.springgraphqlsamples.graphql.enumerated.PostStatus
import java.time.OffsetDateTime
import java.util.*

data class PostPayload(
    var id: String? = null,

    var title: String? = null,

    var content: String? = null,

    var status: PostStatus? = null,

    var createdDate: OffsetDateTime,

    var author: UserPayload? = null,

    var createdBy: String? = null,

    var posts: MutableList<PostPayload>? = null,

    var locale: Locale? = null
)
