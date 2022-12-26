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
            criteria.add(Criteria.where("firstName").regex("^$firstName", "i"))
        }
        if (!lastName.isNullOrBlank()) {
            criteria.add(Criteria.where("lastName").regex("^$lastName", "i"))
        }
        if (!email.isNullOrBlank()) {
            criteria.add(Criteria.where("email").`is`(email))
        }
        if (criteria.isEmpty()) {
            return null
        }
        return Criteria().andOperator(criteria)
    }

    /*
    fun toPredicate(): Predicate? {
        val predicate = BooleanBuilder()
        val qUser = QUser("user")

        if (!username.isNullOrBlank()) {
            predicate.and(qUser.username.eq(username))
        }
        if (!firstName.isNullOrBlank()) {
            predicate.and(qUser.firstName.startsWithIgnoreCase(firstName))
        }
        if (!lastName.isNullOrBlank()) {
            predicate.and(qUser.lastName.startsWithIgnoreCase(lastName))
        }
        if (!email.isNullOrBlank()) {
            predicate.and(qUser.email.eq(email))
        }
        if (!predicate.hasValue()) {
            return null
        }
        return predicate
    }

     */
}
