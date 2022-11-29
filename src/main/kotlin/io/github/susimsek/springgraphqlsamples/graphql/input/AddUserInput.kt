package io.github.susimsek.springgraphqlsamples.graphql.input

import java.util.*

data class AddUserInput(
    var username: String,
    var password: String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var lang: Locale? = null
)
