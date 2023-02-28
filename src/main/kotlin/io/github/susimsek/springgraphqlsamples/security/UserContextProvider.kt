package io.github.susimsek.springgraphqlsamples.security

import io.github.susimsek.springgraphqlsamples.domain.User
import io.github.susimsek.springgraphqlsamples.repository.UserRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class UserContextProvider(
    private val userRepository: UserRepository
) {

    suspend fun getCurrentUser(): User {
        val currentUserId = getCurrentUserLogin().awaitSingleOrNull()
            ?: throw UsernameNotFoundException("User was not found")
        return userRepository.findById(currentUserId) ?: throw UsernameNotFoundException("User was not found")
    }
}
