package io.github.susimsek.springgraphqlsamples.security

import io.github.susimsek.springgraphqlsamples.domain.User
import io.github.susimsek.springgraphqlsamples.exception.UserNotActivatedException
import io.github.susimsek.springgraphqlsamples.repository.UserRepository
import io.github.susimsek.springgraphqlsamples.service.mapper.UserMapper
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component("userDetailsService")
class DomainUserDetailsService(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) : ReactiveUserDetailsService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun findByUsername(login: String): Mono<UserDetails> {
        log.debug("Authenticating $login")

        if (EmailValidator().isValid(login, null)) {
            return userRepository.findOneByEmail(login.lowercase(Locale.ENGLISH))
                .switchIfEmpty(
                    Mono.error(
                        UsernameNotFoundException("User with email $login was not found in the database")
                    )
                )
                .map { createSpringSecurityUser(login, it) }
        }

        val lowercaseLogin = login.lowercase(Locale.ENGLISH)
        return userRepository.findOneByUsername(lowercaseLogin)
            .switchIfEmpty(Mono.error(UsernameNotFoundException("User $lowercaseLogin was not found in the database")))
            .map { createSpringSecurityUser(lowercaseLogin, it) }
    }

    private fun createSpringSecurityUser(lowercaseLogin: String, user: User):
        org.springframework.security.core.userdetails.User {
        if (!user.activated) {
            throw UserNotActivatedException("User $lowercaseLogin was not activated")
        }
        return userMapper.mapSpringSecurityUser(user)
    }
}
