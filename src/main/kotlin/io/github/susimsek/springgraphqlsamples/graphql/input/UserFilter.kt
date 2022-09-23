package io.github.susimsek.springgraphqlsamples.graphql.input

import org.springframework.data.mongodb.core.query.Criteria

data class UserFilter(
    var username: String? = null,

    var firstName: String? = null,

    var lastName: String? = null,

    var email: String? = null
) {
    fun toCriteria(): Criteria? {
        val criteria = mutableListOf<Criteria>()

        if (!username.isNullOrBlank()) {
            criteria.add(Criteria.where("username").`is`(username))
        }
        if (!firstName.isNullOrBlank()) {
            criteria.add(Criteria.where("firstName").regex("^$firstName"))
        }
        if (!lastName.isNullOrBlank()) {
            criteria.add(Criteria.where("lastName").regex("^$lastName"))
        }
        if (!email.isNullOrBlank()) {
            criteria.add(Criteria.where("email").`is`(email))
        }
        if (criteria.isEmpty()) {
            return null
        }
        return Criteria().andOperator(criteria)
    }
}
