package io.github.susimsek.springgraphqlsamples.graphql.directive

import graphql.schema.DataFetchingEnvironment
import java.util.*

class UppercaseDirective : StringFormatDirective() {
    override fun format(env: DataFetchingEnvironment, value: String): String {
        return value.uppercase(env.locale)
    }
}
