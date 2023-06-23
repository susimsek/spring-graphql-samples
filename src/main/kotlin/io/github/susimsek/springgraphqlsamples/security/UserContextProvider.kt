package io.github.susimsek.springgraphqlsamples.security

import io.github.susimsek.springgraphqlsamples.domain.User
import io.github.susimsek.springgraphqlsamples.repository.UserRepository
import io.github.susimsek.springgraphqlsamples.service.mapper.UserMapper
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class UserContextProvider(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) {

    suspend fun getCurrentUser(): User {
        val currentUserId = getCurrentUserLogin().awaitSingleOrNull()
            ?: throw UsernameNotFoundException("User was not found")
        return userRepository.findById(currentUserId) ?: throw UsernameNotFoundException("User was not found")
    }

    suspend fun getUserDetails(userId: String): org.springframework.security.core.userdetails.User {
        val user = userRepository.findById(userId) ?: throw UsernameNotFoundException("User was not found")
        return userMapper.mapSpringSecurityUser(user)
    }
}
