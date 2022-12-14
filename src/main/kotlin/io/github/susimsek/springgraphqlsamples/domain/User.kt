package io.github.susimsek.springgraphqlsamples.domain

import com.fasterxml.jackson.annotation.JsonIgnore
// import com.querydsl.core.annotations.QueryEntity
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.Locale

// @QueryEntity
@Document(collection = "user")
data class User(
    @Indexed
    var username: String = "",

    @JsonIgnore
    var password: String = "",

    @Field("first_name")
    var firstName: String = "",

    @Field("last_name")
    var lastName: String = "",

    @Field("lang")
    var lang: Locale = Locale.ENGLISH,

    @Indexed
    var email: String = "",

    var activated: Boolean = false,

    ) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun toString(): String {
        return "User(id=$id, " +
                "username=$username}, " +
                "password=$password, " +
                "firstName=${firstName}, " +
                "lastName=${lastName}, " +
                "email=${email}, " +
                "activated=$activated)"
    }
}
