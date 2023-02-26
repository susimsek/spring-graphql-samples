package io.github.susimsek.springgraphqlsamples.service.dto

import io.github.susimsek.springgraphqlsamples.domain.User

data class EmailSenderDTO(
    var to: User,
    var subject: String,
    var content: String,
    var isMultipart: Boolean,
    var isHtml: Boolean
)
