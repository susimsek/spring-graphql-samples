package io.github.susimsek.springgraphqlsamples.graphql.input

import io.github.susimsek.springgraphqlsamples.graphql.enumerated.OrderType
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.PostOrderField

data class PostOrder(
    var field: PostOrderField,
    var order: OrderType? = null
)