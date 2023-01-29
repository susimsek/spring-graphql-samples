package io.github.susimsek.springgraphqlsamples.domain

import io.github.susimsek.springgraphqlsamples.graphql.enumerated.RoleName
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "role")
data class Role(
    @Indexed(unique = true)
    var name: RoleName
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Role) return false
        return name == other.name
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}