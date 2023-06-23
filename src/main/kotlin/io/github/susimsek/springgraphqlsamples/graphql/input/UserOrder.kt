package io.github.susimsek.springgraphqlsamples.graphql.input

import io.github.susimsek.springgraphqlsamples.graphql.enumerated.OrderType
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.UserOrderField

data class UserOrder(
    var field: UserOrderField,
    var order: OrderType? = null
)
