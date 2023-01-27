package io.github.susimsek.springgraphqlsamples.graphql.type

import io.github.susimsek.springgraphqlsamples.graphql.enumerated.PostStatus
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.*

data class PostPayload(
    val id: String? = null,

    var title: String? = null,

    var content: String? = null,

    var status: PostStatus? = null,

    var createdAt: OffsetDateTime? = null,

    var author: UserPayload? = null,

    var createdBy: String? = null,

    var posts: MutableList<PostPayload>? = null,

    var locale: Locale? = null
): Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}
