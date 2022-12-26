package io.github.susimsek.springgraphqlsamples.service

import io.github.susimsek.springgraphqlsamples.exception.EMAIL_ALREADY_EXISTS_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.ResourceNotFoundException
import io.github.susimsek.springgraphqlsamples.exception.USERNAME_ALREADY_EXISTS_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.USER_NOT_FOUND_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.ValidationException
import io.github.susimsek.springgraphqlsamples.graphql.input.AddUserInput
import io.github.susimsek.springgraphqlsamples.graphql.input.UserFilter
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import io.github.susimsek.springgraphqlsamples.graphql.type.UserSearchResult
import io.github.susimsek.springgraphqlsamples.repository.UserRepository
import io.github.susimsek.springgraphqlsamples.security.getCurrentUserLogin
import io.github.susimsek.springgraphqlsamples.service.mapper.UserMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userMapper: UserMapper
) {

    suspend fun createUser(input: AddUserInput): UserPayload {
        var existingUser = userRepository.findOneByUsername(input.username).awaitSingleOrNull()
        existingUser?.let {
           if (!it.activated) {
               userRepository.delete(it)
           } else {
               throw ValidationException(USERNAME_ALREADY_EXISTS_MSG_CODE)
           }
        }

        existingUser = userRepository.findOneByEmailIgnoreCase(input.email).awaitSingleOrNull()

        existingUser?.let {
            if (!it.activated) {
                userRepository.delete(it)
            } else {
                throw ValidationException(EMAIL_ALREADY_EXISTS_MSG_CODE)
            }
        }

        val encryptedPassword = passwordEncoder.encode(input.password)
        input.username = input.username.lowercase(Locale.getDefault())
        input.email = input.email.lowercase(Locale.getDefault())
        input.password = encryptedPassword
        var user = userMapper.toEntity(input)
        user.activated = true

        user = userRepository.save(user)

        return userMapper.toType(user)
    }


    suspend fun getUsers(pageRequest: Pageable, filter: UserFilter?): UserSearchResult {
        return userRepository.findAllByFilter(filter, pageRequest)
            .map{it.map(userMapper::toType)}
            .map{UserSearchResult(it)}
            .awaitSingle()
    }



    /*
    suspend fun getUsers(pageRequest: Pageable, filter: UserFilter?): UserSearchResult {
        return userRepository.findBy<User, Page<User>, Mono<Page<User>>>(
            filter?.toPredicate() ?: BooleanBuilder(),
        ) {
            it.page(
                pageRequest
            )
        }.map{it.map(userMapper::toType)}
            .map{UserSearchResult(it)}
            .awaitSingle()
    }

     */

    suspend fun getCurrentUser(): UserPayload {
        val currentUserId = getCurrentUserLogin().awaitSingleOrNull()
        ?: throw UsernameNotFoundException("User was not found")
        val user = userRepository.findById(currentUserId) ?: throw UsernameNotFoundException("User was not found")
        return userMapper.toType(user)
    }

    suspend fun getName(user: UserPayload): String {
        return Mono.just(user.firstName + " " + user.lastName)
            .awaitSingle()
    }

    suspend fun getUser(id: String): UserPayload {
        val user = userRepository.findById(id)
            ?: throw ResourceNotFoundException(USER_NOT_FOUND_MSG_CODE, arrayOf(id))
        return userMapper.toType(user)
    }

    fun getUserByIdIn(ids: MutableSet<String>): Flow<UserPayload> {
        return userRepository.findAllByIdIn(ids)
            .map(userMapper::toType)
    }
}
