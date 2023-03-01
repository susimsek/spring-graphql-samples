package io.github.susimsek.springgraphqlsamples.domain

// import com.querydsl.core.annotations.QueryEntity
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.OffsetDateTime
import java.util.*

// @QueryEntity
@Document(collection = "user")
data class User(

    @Indexed(unique = true)
    var username: String = "",

    @JsonIgnore
    var password: String = "",

    @Field("first_name")
    var firstName: String = "",

    @Field("last_name")
    var lastName: String = "",

    @Field("lang")
    var lang: Locale = Locale.ENGLISH,

    @Indexed(unique = true)
    var email: String = "",

    var activated: Boolean = false,

    var roles: MutableSet<Role> = mutableSetOf(),

    var activationToken: String? = null,

    var activationTokenExpiryDate: OffsetDateTime? = null,

    var resetToken: String? = null,

    var resetDate: OffsetDateTime? = null,

    var resetTokenExpiryDate: OffsetDateTime? = null

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
            "firstName=$firstName, " +
            "lastName=$lastName, " +
            "lang=$email, " +
            "email=$email, " +
            "activated=$activated)"
    }
}
