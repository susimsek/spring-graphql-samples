package io.github.susimsek.springgraphqlsamples.graphql.type

import java.math.BigDecimal
import java.time.LocalDate


data class Message(
    val content: Map<String, Any>,
    val sentDate: LocalDate,
    val price: BigDecimal
)