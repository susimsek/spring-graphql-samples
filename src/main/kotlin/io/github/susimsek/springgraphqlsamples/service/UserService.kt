package io.github.susimsek.springgraphqlsamples.service

import io.github.susimsek.springgraphqlsamples.exception.EMAIL_ALREADY_EXISTS_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.ResourceAlreadyExistsException
import io.github.susimsek.springgraphqlsamples.exception.ResourceNotFoundException
import io.github.susimsek.springgraphqlsamples.exception.USERNAME_ALREADY_EXISTS_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.USER_NOT_FOUND_MSG_CODE
import io.github.susimsek.springgraphqlsamples.graphql.input.AddUserInput
import io.github.susimsek.springgraphqlsamples.graphql.input.UserFilter
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import io.github.susimsek.springgraphqlsamples.repository.UserRepository
import io.github.susimsek.springgraphqlsamples.security.getCurrentUserLogin
import io.github.susimsek.springgraphqlsamples.service.mapper.UserMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.Locale

@Component
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userMapper: UserMapper
) {

    suspend fun createUser(input: AddUserInput): UserPayload {
        return userRepository.findOneByUsername(input.username)
            .flatMap { existingUser ->
                if (!existingUser.activated) {
                    userRepository.delete(existingUser)
                } else {
                    Mono.error(ResourceAlreadyExistsException(USERNAME_ALREADY_EXISTS_MSG_CODE))
                }
            }
            .then(userRepository.findOneByEmailIgnoreCase(input.email))
            .flatMap { existingUser ->
                if (!existingUser.activated) {
                    userRepository.delete(existingUser)
                } else {
                    Mono.error(ResourceAlreadyExistsException(EMAIL_ALREADY_EXISTS_MSG_CODE))
                }
            }.then(
                Mono.fromCallable {
                val encryptedPassword = passwordEncoder.encode(input.password)
                input.username = input.username.lowercase(Locale.getDefault())
                input.email = input.email.lowercase(Locale.getDefault())
                input.password = encryptedPassword
                val user = userMapper.toEntity(input)
                user.activated = true
                user
            }
            ).flatMap(userRepository::save)
            .map(userMapper::toType)
            .awaitSingle()
    }

    fun getUsers(pageRequest: Pageable, filter: UserFilter?): Flow<UserPayload> {
        return userRepository.findAllByFilter(filter, pageRequest)
            .map(userMapper::toType)
            .asFlow()
    }

    suspend fun getCurrentUser(): UserPayload {
        return getCurrentUserLogin().flatMap(userRepository::findById)
            .map(userMapper::toType)
            .switchIfEmpty(Mono.error(UsernameNotFoundException("User was not found")))
            .awaitSingle()
    }

    suspend fun getName(user: UserPayload): String {
        return Mono.just(user.firstName + " " + user.lastName)
            .awaitSingle()
    }

    fun getUser(id: String): Mono<UserPayload> {
        return userRepository.findById(id)
            .map(userMapper::toType)
            .switchIfEmpty(
                Mono.error(
                    (
                        ResourceNotFoundException(
                USER_NOT_FOUND_MSG_CODE, arrayOf(id)
                        )
                    )
                )
            )
    }

    fun getUserByIdIn(ids: MutableSet<String>?): Flux<UserPayload> {
        return userRepository.findAllByIdIn(ids)
            .map(userMapper::toType)
    }
}
