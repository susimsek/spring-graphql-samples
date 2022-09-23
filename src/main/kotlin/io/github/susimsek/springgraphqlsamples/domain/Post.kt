package io.github.susimsek.springgraphqlsamples.domain

import io.github.susimsek.springgraphqlsamples.graphql.enumerated.PostStatus
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "post")
data class Post(

    var title: String? = null,

    @Indexed
    var content: String? = null,

    @Indexed
    var status: PostStatus? = null,
) : BaseEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Post) return false
        return id != null && other.id != null && id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun toString(): String {
        return "Post(title=$title, content=$content, status=$status)"
    }
}
