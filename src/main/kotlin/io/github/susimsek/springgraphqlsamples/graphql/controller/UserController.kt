package io.github.susimsek.springgraphqlsamples.graphql.controller

import io.github.susimsek.springgraphqlsamples.graphql.DEFAULT_PAGE_NO
import io.github.susimsek.springgraphqlsamples.graphql.DEFAULT_SIZE
import io.github.susimsek.springgraphqlsamples.graphql.MAX_SIZE
import io.github.susimsek.springgraphqlsamples.graphql.input.AddUserInput
import io.github.susimsek.springgraphqlsamples.graphql.input.UserFilter
import io.github.susimsek.springgraphqlsamples.graphql.input.UserOrder
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import io.github.susimsek.springgraphqlsamples.service.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid


@Controller
class UserController(private val userService: UserService) {
    @MutationMapping
    fun createUser(@Argument @Valid input: AddUserInput): Mono<UserPayload> {
        return userService.createUser(input)
    }

    @QueryMapping
    fun users(
        @Argument page: Int?,
        @Argument size: Int?,
        @Argument filter: UserFilter?,
        @Argument orders: MutableList<UserOrder>?
    ): Flux<UserPayload> {
        val pageNo = page ?: DEFAULT_PAGE_NO
        val sizeNo = (size ?: DEFAULT_SIZE).coerceAtMost(MAX_SIZE)
        val sort = orders?.map(UserOrder::toOrder)?.let { Sort.by(it) } ?: Sort.unsorted()
        val pageRequest = PageRequest.of(pageNo, sizeNo, sort)
        return userService.getUsers(pageRequest, filter)
    }

    @QueryMapping
    fun me(): Mono<UserPayload> {
        return userService.getCurrentUser()
    }

    @SchemaMapping
    fun name(user: UserPayload): Mono<String> {
        return userService.getName(user)
    }
}
