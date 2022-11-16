package io.github.susimsek.springgraphqlsamples.graphql.controller

import io.github.susimsek.springgraphqlsamples.graphql.DEFAULT_PAGE_NO
import io.github.susimsek.springgraphqlsamples.graphql.DEFAULT_SIZE
import io.github.susimsek.springgraphqlsamples.graphql.MAX_SIZE
import io.github.susimsek.springgraphqlsamples.graphql.input.AddUserInput
import io.github.susimsek.springgraphqlsamples.graphql.input.UserFilter
import io.github.susimsek.springgraphqlsamples.graphql.input.UserOrder
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import io.github.susimsek.springgraphqlsamples.service.UserService
import jakarta.validation.Valid
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import java.util.Locale


@Controller
class UserController(private val userService: UserService) {
    @MutationMapping
    suspend fun createUser(@Argument @Valid input: AddUserInput): UserPayload {
        return userService.createUser(input)
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    suspend fun users(
        @Argument page: Int?,
        @Argument size: Int?,
        @Argument filter: UserFilter?,
        @Argument orders: MutableList<UserOrder>?
    ): List<UserPayload> {
        val pageNo = page ?: DEFAULT_PAGE_NO
        val sizeNo = (size ?: DEFAULT_SIZE).coerceAtMost(MAX_SIZE)
        val sort = orders?.map(UserOrder::toOrder)?.let { Sort.by(it) } ?: Sort.unsorted()
        val pageRequest = PageRequest.of(pageNo, sizeNo, sort)
        return userService.getUsers(pageRequest, filter)
            .toList()
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    suspend fun me(locale: Locale): UserPayload {
        val user = userService.getCurrentUser()
        user.locale = locale
        return user
    }

    @SchemaMapping
    suspend fun name(user: UserPayload): String {
        return userService.getName(user)
    }
}
