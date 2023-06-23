package io.github.susimsek.springgraphqlsamples.domain

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "passwordResetToken")
open class PasswordResetToken(
    var userId: String
) : Token()
