package io.github.susimsek.springgraphqlsamples.domain

import io.github.susimsek.springgraphqlsamples.graphql.enumerated.RoleName
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable

@Document(collection = "role")
data class Role(
    @Indexed(unique = true)
    var name: RoleName
) : BaseEntity(), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Role) return false
        return name == other.name
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
