package io.github.susimsek.springgraphqlsamples.domain

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "verificationToken")
open class VerificationToken(
    var userId: String
) : Token()
