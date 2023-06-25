package io.github.susimsek.springgraphqlsamples.service

import io.github.susimsek.springgraphqlsamples.domain.User
import io.github.susimsek.springgraphqlsamples.exception.EMAIL_ALREADY_EXISTS_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.PASSWORD_INVALID_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.ROLE_NAME_NOT_FOUND_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.ResourceNotFoundException
import io.github.susimsek.springgraphqlsamples.exception.USERNAME_ALREADY_EXISTS_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.USER_NOT_FOUND_MSG_CODE
import io.github.susimsek.springgraphqlsamples.exception.ValidationException
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.RoleName
import io.github.susimsek.springgraphqlsamples.graphql.input.AddUserInput
import io.github.susimsek.springgraphqlsamples.graphql.input.UserFilter
import io.github.susimsek.springgraphqlsamples.graphql.type.PagedEntityModel
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import io.github.susimsek.springgraphqlsamples.repository.RoleRepository
import io.github.susimsek.springgraphqlsamples.repository.UserRepository
import io.github.susimsek.springgraphqlsamples.repository.redis.UserRedisRepository
import io.github.susimsek.springgraphqlsamples.security.UserContextProvider
import io.github.susimsek.springgraphqlsamples.service.mapper.UserMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userMapper: UserMapper,
    private val roleRepository: RoleRepository,
    private val mailService: MailService,
    private val userContextProvider: UserContextProvider,
    private val passwordResetTokenService: PasswordResetTokenService,
    private val verificationTokenService: VerificationTokenService,
    private val userRedisRepository: UserRedisRepository
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

        existingUser = userRepository.findOneByEmail(input.email.lowercase(Locale.ENGLISH)).awaitSingleOrNull()

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

        user.apply {
            activated = false
            val role = roleRepository.findByName(RoleName.ROLE_USER)
                ?: throw ResourceNotFoundException(ROLE_NAME_NOT_FOUND_MSG_CODE, arrayOf(RoleName.ROLE_USER))
            roles = mutableSetOf(role)
        }

        val activationToken = UUID.randomUUID().toString()
        verificationTokenService.createToken(user.id, activationToken)

        user = userRepository.save(user)
        mailService.sendActivationEmail(user, activationToken)
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
        val userId = userContextProvider.getCurrentUserId()
        val cache = userRedisRepository.findById(userId)
        if (cache != null) {
            return cache
        }
        val user = userContextProvider.getCurrentUserById(userId)
        val payload = userMapper.toType(user)
        userRedisRepository.save(user.id, payload)
        return payload
    }

    suspend fun getName(user: UserPayload): String {
        return Mono.just(user.firstName + " " + user.lastName)
            .awaitSingle()
    }

    suspend fun getUser(id: String): UserPayload {
        val cache = userRedisRepository.findById(id)
        if (cache != null) {
            return cache
        }
        val user = userRepository.findById(id)
            ?: throw ResourceNotFoundException(USER_NOT_FOUND_MSG_CODE, arrayOf(id))
        val payload = userMapper.toType(user)
        userRedisRepository.save(user.id, payload)
        return payload
    }

    suspend fun findById(id: String): User? {
        return userRepository.findById(id)
    }

    fun getUserByIdIn(ids: MutableSet<String>): Flow<UserPayload> {
        return userRepository.findAllByIdIn(ids)
            .map(userMapper::toType)
    }

    @Transactional
    suspend fun activateAccount(token: String): Boolean {
        log.debug("Activating user for activation token {}", token)
        val verificationToken = verificationTokenService.findByToken(token) ?: return false
        val user = findById(verificationToken.userId) ?: return false
        val isValid = verificationTokenService.verifyExpiration(verificationToken)
        if (!isValid) {
            return false
        }
        user.activated = true
        userRepository.save(user)
        verificationTokenService.delete(verificationToken)

        log.debug("Activated user: {}", user)
        return true
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Boolean {
        val user = userContextProvider.getCurrentUser()
        val currentEncryptedPassword = user.password
        if (!passwordEncoder.matches(currentPassword, currentEncryptedPassword)) {
            throw ValidationException(PASSWORD_INVALID_MSG_CODE)
        }
        val encryptedPassword = passwordEncoder.encode(newPassword)
        user.password = encryptedPassword
        userRepository.save(user)

        log.debug("Changed password for User: {}", user)
        return true
    }

    suspend fun forgotPassword(email: String): Boolean {
        val user = userRepository.findOneByEmail(email.lowercase(Locale.ENGLISH)).awaitSingleOrNull()
        if (user != null && user.activated) {
            val resetToken = UUID.randomUUID().toString()
            passwordResetTokenService.createToken(user.id, resetToken)
            mailService.sendPasswordResetMail(user, resetToken)
        }
        return true
    }

    @Transactional
    suspend fun resetPassword(token: String, newPassword: String): Boolean {
        log.debug("Reset user password for reset token {}", token)
        val resetToken = passwordResetTokenService.findByToken(token) ?: return false
        val user = findById(resetToken.userId) ?: return false
        val isValid = passwordResetTokenService.verifyExpiration(resetToken)
        if (!isValid) {
            return false
        }
        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)
        passwordResetTokenService.delete(resetToken)
        return true
    }
}
