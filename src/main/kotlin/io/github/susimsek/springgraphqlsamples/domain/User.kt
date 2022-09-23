package io.github.susimsek.springgraphqlsamples.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.querydsl.core.annotations.QueryEntity
import io.github.susimsek.springgraphqlsamples.domain.audit.AbstractAuditingEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@QueryEntity
@Document(collection = "user")
data class User(
    @Id
    var id: String? = null,

    @Indexed
    var username: String? = null,

    @JsonIgnore
    var password: String? = null,

    @Field("first_name")
    var firstName: String? = null,

    @Field("last_name")
    var lastName: String? = null,

    @Indexed
    var email: String? = null,

    var activated: Boolean? = false,

    ) : AbstractAuditingEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return id != null && other.id != null && id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun toString(): String {
        return "User(id=$id, " +
                "username=$username, " +
                "password=$password, " +
                "firstName=$firstName, " +
                "lastName=$lastName, " +
                "email=$email, " +
                "activated=$activated)"
    }
}
