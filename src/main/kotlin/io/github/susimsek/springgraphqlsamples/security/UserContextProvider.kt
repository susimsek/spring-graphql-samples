package io.github.susimsek.springgraphqlsamples.security

import io.github.susimsek.springgraphqlsamples.domain.User
import io.github.susimsek.springgraphqlsamples.repository.UserRepository
import io.github.susimsek.springgraphqlsamples.service.mapper.UserMapper
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

private const val USERNAME_NOT_FOUND_MSG = "User was not found"

@Component
class UserContextProvider(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) {

    suspend fun getCurrentUserId(): String {
        return getCurrentUserLogin().awaitSingleOrNull()
            ?: throw UsernameNotFoundException(USERNAME_NOT_FOUND_MSG)
    }

    suspend fun getCurrentUser(): User {
        val currentUserId = getCurrentUserId()
        return userRepository.findById(currentUserId) ?: throw UsernameNotFoundException(USERNAME_NOT_FOUND_MSG)
    }

    suspend fun getUserDetails(userId: String): org.springframework.security.core.userdetails.User {
        val user = userRepository.findById(userId) ?: throw UsernameNotFoundException(USERNAME_NOT_FOUND_MSG)
        return userMapper.mapSpringSecurityUser(user)
    }
}
