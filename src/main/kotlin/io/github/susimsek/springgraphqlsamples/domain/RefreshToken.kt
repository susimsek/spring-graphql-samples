package io.github.susimsek.springgraphqlsamples.domain

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "refreshToken")
open class RefreshToken(
    var userId: String
) : Token()
