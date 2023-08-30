package io.github.susimsek.springgraphqlsamples.graphql.type

import graphql.scalars.country.code.CountryCode
import java.math.BigDecimal
import java.net.URL
import java.time.LocalDate
import java.util.*

data class Message(
    val id: UUID,
    val url: URL,
    val content: Map<String, Any>,
    val sentDate: LocalDate,
    val price: BigDecimal,
    val currency: Currency,
    val countryCode: CountryCode
)
