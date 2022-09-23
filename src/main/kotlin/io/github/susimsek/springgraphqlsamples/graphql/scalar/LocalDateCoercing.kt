package io.github.susimsek.springgraphqlsamples.graphql.scalar

import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateCoercing(
    private val formatter: DateTimeFormatter
) : Coercing<LocalDate, String?> {

    @Throws(CoercingSerializeException::class)
    override fun serialize(dataFetcherResult: Any): String? {
        if (dataFetcherResult is LocalDate) {
            return dataFetcherResult.format(formatter)
        } else {
            throw CoercingSerializeException("$dataFetcherResult, Not a valid LocalDate")
        }
    }

    @Throws(CoercingParseValueException::class)
    override fun parseValue(input: Any): LocalDate {
        return LocalDate.parse(input.toString(), formatter)
    }

    @Throws(CoercingParseLiteralException::class)
    override fun parseLiteral(input: Any): LocalDate {
        if (input is StringValue) {
            return LocalDate.parse(input.value, formatter)
        }
        throw CoercingParseLiteralException("$input, Value is not a valid ISO LocalDate")
    }
}
