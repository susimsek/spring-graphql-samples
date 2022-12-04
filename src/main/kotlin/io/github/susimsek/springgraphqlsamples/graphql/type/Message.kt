package io.github.susimsek.springgraphqlsamples.graphql.type

import java.time.LocalDate


data class Message(
    val content: Map<String, Any>,
    val sentDate: LocalDate
)