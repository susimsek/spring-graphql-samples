package io.github.susimsek.springgraphqlsamples.graphql

import graphql.schema.DataFetchingEnvironment
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.OrderType
import jakarta.validation.constraints.NotNull
import org.springframework.data.domain.Sort
import org.springframework.graphql.data.query.SortStrategy

class GraphqlSortStrategy : SortStrategy {

    override fun extract(environment: DataFetchingEnvironment): Sort {
        val orders: List<Map<String, Any?>>? = environment.getArgument("orders")
        return when (orders.isNullOrEmpty()) {
            true -> Sort.unsorted()
            else -> orders.map(this::resolveOrder)
                .let(Sort::by)
        }
    }

    private fun getDirection(order: Map<String, Any?>): Sort.Direction {
        val orderType = order["order"] as String? ?: return Sort.DEFAULT_DIRECTION
        return when (OrderType.valueOf(orderType)) {
            OrderType.DESC -> Sort.Direction.DESC
            else -> Sort.Direction.ASC
        }
    }

    private fun resolveOrder(@NotNull order: Map<String, Any?>): Sort.Order {
        val direction = getDirection(order)
        val field = order["field"] as String
        return Sort.Order(direction, field)
    }
}
