package io.github.susimsek.springgraphqlsamples.graphql.input

import io.github.susimsek.springgraphqlsamples.graphql.enumerated.OrderType
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.UserOrderField
import org.springframework.data.domain.Sort

data class UserOrder(
    var field: UserOrderField? = null,
    var order: OrderType? = null
) {

    fun toOrder(): Sort.Order {
        val direction: Sort.Direction =
            if (order == null) Sort.DEFAULT_DIRECTION
            else if (order === OrderType.ASC) Sort.Direction.ASC else Sort.Direction.DESC
        return Sort.Order(direction, field!!.name)
    }
}
