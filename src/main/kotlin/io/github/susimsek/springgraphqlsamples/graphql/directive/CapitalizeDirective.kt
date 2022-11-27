package io.github.susimsek.springgraphqlsamples.graphql.directive

import graphql.schema.DataFetchingEnvironment
import io.github.susimsek.springgraphqlsamples.util.capitalize

class CapitalizeDirective : StringFormatDirective() {
    override fun format(env: DataFetchingEnvironment, value: String): String {
        return value.capitalize(env.locale)
    }
}
