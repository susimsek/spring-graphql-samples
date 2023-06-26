package io.github.susimsek.springgraphqlsamples.graphql.controller

import io.github.susimsek.springgraphqlsamples.graphql.DEFAULT_PAGE_NO
import io.github.susimsek.springgraphqlsamples.graphql.DEFAULT_SIZE
import io.github.susimsek.springgraphqlsamples.graphql.MAX_SIZE
import io.github.susimsek.springgraphqlsamples.graphql.input.AddUserInput
import io.github.susimsek.springgraphqlsamples.graphql.input.ChangePasswordInput
import io.github.susimsek.springgraphqlsamples.graphql.input.ResetPasswordInput
import io.github.susimsek.springgraphqlsamples.graphql.input.UserFilter
import io.github.susimsek.springgraphqlsamples.graphql.type.PagedEntityModel
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import io.github.susimsek.springgraphqlsamples.security.recaptcha.RecaptchaService
import io.github.susimsek.springgraphqlsamples.service.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.ContextValue
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller

@Controller
class UserController(
    private val userService: UserService,
    private val recaptchaService: RecaptchaService
) {
    @MutationMapping
    suspend fun createUser(
        @Argument
        input: AddUserInput,
        @ContextValue(required = false) recaptcha: String?
    ): UserPayload {
        recaptchaService.validateToken(recaptcha)
        return userService.createUser(input)
    }

    @MutationMapping
    suspend fun activateAccount(@Argument token: String): Boolean {
        return userService.activateAccount(token)
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    suspend fun changePassword(@Argument input: ChangePasswordInput): Boolean {
        return userService.changePassword(input.currentPassword, input.newPassword)
    }

    @MutationMapping
    suspend fun forgotPassword(
        @Argument email: String,
        @ContextValue(required = false) recaptcha: String?
    ): Boolean {
        recaptchaService.validateToken(recaptcha)
        return userService.forgotPassword(email)
    }

    @MutationMapping
    suspend fun resetPassword(@Argument input: ResetPasswordInput): Boolean {
        return userService.resetPassword(input.token, input.newPassword)
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    suspend fun users(
        @Argument page: Int?,
        @Argument size: Int?,
        @Argument filter: UserFilter?,
        sort: Sort
    ): PagedEntityModel<UserPayload> {
        val pageNo = page ?: DEFAULT_PAGE_NO
        val sizeNo = (size ?: DEFAULT_SIZE).coerceAtMost(MAX_SIZE)
        val pageRequest = PageRequest.of(pageNo, sizeNo, sort)
        return userService.getUsers(pageRequest, filter)
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    suspend fun me(): UserPayload {
        return userService.getCurrentUser()
    }

    @SchemaMapping
    suspend fun name(user: UserPayload): String {
        return userService.getName(user)
    }
}
