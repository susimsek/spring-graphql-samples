package io.github.susimsek.springgraphqlsamples.graphql.type

import java.math.BigDecimal
import java.net.URL
import java.time.LocalDate
import java.util.UUID

data class Message(
    val id: UUID,
    val url: URL,
    val content: Map<String, Any>,
    val sentDate: LocalDate,
    val price: BigDecimal
)
