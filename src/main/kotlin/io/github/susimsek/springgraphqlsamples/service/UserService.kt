package io.github.susimsek.springgraphqlsamples.service

import io.github.susimsek.springgraphqlsamples.domain.User
import io.github.susimsek.springgraphqlsamples.exception.EMAIL_ALREADY_EXISTS_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.ROLE_NAME_NOT_FOUND_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.ResourceNotFoundException
import io.github.susimsek.springgraphqlsamples.exception.USERNAME_ALREADY_EXISTS_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.USER_NOT_FOUND_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.ValidationException
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.RoleName
import io.github.susimsek.springgraphqlsamples.graphql.input.AddUserInput
import io.github.susimsek.springgraphqlsamples.graphql.input.UserFilter
import io.github.susimsek.springgraphqlsamples.graphql.type.PagedEntityModel
import io.github.susimsek.springgraphqlsamples.graphql.type.Token
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import io.github.susimsek.springgraphqlsamples.repository.RoleRepository
import io.github.susimsek.springgraphqlsamples.repository.UserRepository
import io.github.susimsek.springgraphqlsamples.security.getCurrentUserLogin
import io.github.susimsek.springgraphqlsamples.service.mapper.UserMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userMapper: UserMapper,
    private val roleRepository: RoleRepository,
    private val mailService: MailService
) {

    private val log = LoggerFactory.getLogger(javaClass)

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

        val role = roleRepository.findByName(RoleName.ROLE_USER)
            ?: throw ResourceNotFoundException(ROLE_NAME_NOT_FOUND_MSG_CODE, arrayOf(RoleName.ROLE_USER))
        user.roles = mutableSetOf(role)
        user.activated = false

        val activationToken = UUID.randomUUID().toString()
        createVerificationToken(user, activationToken)

        user = userRepository.save(user)
        mailService.sendActivationEmail(user)
        return userMapper.toType(user)
    }

    suspend fun getUsers(pageRequest: Pageable, filter: UserFilter?): PagedEntityModel<UserPayload> {
        val result = userRepository.findAllByFilter(filter, pageRequest)
            .map(userMapper::toType)
        return PagedEntityModel<UserPayload>(result)
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

    suspend fun activateAccount(token: String): Boolean {
        log.debug("Activating user for activation token {}", token)
        val user = userRepository.findByActivationToken(token) ?: return false
        val isExpired = user.activationTokenExpiryDate?.isAfter(OffsetDateTime.now()) == false
        if (user.activated || isExpired) {
            return false
        }
        user.activated = true
        user.activationToken = null
        user.activationTokenExpiryDate = null
        userRepository.save(user)

        log.debug("Activated user: {}", user)
        return true
    }

    fun createVerificationToken(user: User, token: String) {
        user.activationToken = token
        user.activationTokenExpiryDate = OffsetDateTime.now().plusMinutes(60 * 24)
    }
}
