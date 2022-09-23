package io.github.susimsek.springgraphqlsamples.service

import io.github.susimsek.springgraphqlsamples.graphql.input.AddUserInput
import io.github.susimsek.springgraphqlsamples.graphql.input.UserFilter
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import io.github.susimsek.springgraphqlsamples.repository.UserRepository
import io.github.susimsek.springgraphqlsamples.security.getCurrentUserLogin
import io.github.susimsek.springgraphqlsamples.service.mapper.UserMapper
import io.github.susimsek.springgraphqlsamples.exception.ResourceAlreadyExistsException
import io.github.susimsek.springgraphqlsamples.exception.ResourceNotFoundException
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
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

    fun createUser(input: AddUserInput): Mono<UserPayload> {
        return userRepository.findOneByUsername(input.username!!)
            .flatMap { existingUser ->
                if (existingUser.activated == false) {
                    userRepository.delete(existingUser)
                } else {
                    Mono.error(ResourceAlreadyExistsException("Username is already in use!"))
                }
            }
            .then(userRepository.findOneByEmailIgnoreCase(input.email!!))
            .flatMap { existingUser ->
                if (existingUser.activated == false) {
                    userRepository.delete(existingUser)
                } else {
                    Mono.error(ResourceAlreadyExistsException("Email is already in use!"))
                }
            }.then(Mono.fromCallable {
                val encryptedPassword = passwordEncoder.encode(input.password)
                input.username = input.username?.lowercase(Locale.getDefault())
                input.email = input.email?.lowercase(Locale.getDefault())
                input.password = encryptedPassword
                val user = userMapper.toEntity(input)
                user.activated = true
                user
            }).flatMap(userRepository::save)
            .map(userMapper::toType)
    }

    @PreAuthorize("isAuthenticated()")
    fun getUsers(pageRequest: Pageable, filter: UserFilter?): Flux<UserPayload> {
        return userRepository.findAllByFilter(filter, pageRequest)
            .map(userMapper::toType)
    }

    @PreAuthorize("isAuthenticated()")
    fun getCurrentUser(): Mono<UserPayload> {
        return getCurrentUserLogin().flatMap(userRepository::findById)
            .map(userMapper::toType)
            .switchIfEmpty(Mono.error(UsernameNotFoundException("User was not found")))
    }


    fun getName(user: UserPayload): Mono<String> {
        return Mono.just(user.firstName + " " + user.lastName)
    }

    @PreAuthorize("isAuthenticated()")
    fun getUser(id: String): Mono<UserPayload> {
        return userRepository.findById(id)
            .map(userMapper::toType)
            .switchIfEmpty(Mono.error((ResourceNotFoundException("User with id $id was not found"))))
    }

    fun getUserByIdIn(ids: MutableSet<String>?): Flux<UserPayload> {
        return userRepository.findAllByIdIn(ids)
            .map(userMapper::toType)
    }
}
