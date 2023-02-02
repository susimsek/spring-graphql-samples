@file:JvmName("SecurityUtils")

package io.github.susimsek.springgraphqlsamples.security

import org.springframework.security.config.Elements.ANONYMOUS
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.Jwt
import reactor.core.publisher.Mono

/**
 * Get the login of the current user.
 *
 * @return the login of the current user.
 */
fun getCurrentUserLogin(): Mono<String> =
    ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .flatMap { Mono.justOrEmpty(extractPrincipal(it)) }

fun extractPrincipal(authentication: Authentication?): String? {
    if (authentication == null) {
        return null
    }

    return when (val principal = authentication.principal) {
        is UserDetails -> principal.username
        is Jwt -> principal.subject
        is String -> principal
        else -> null
    }
}

/**
 * Get the JWT of the current user.
 *
 * @return the JWT of the current user.
 */
fun getCurrentUserJWT(): Mono<String> =
    ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .filter { it.credentials is String }
        .map { it.credentials as String }

/**
 * Check if a user is authenticated.
 *
 * @return true if the user is authenticated, false otherwise.
 */
fun isAuthenticated(): Mono<Boolean> {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(Authentication::getAuthorities)
        .map {
            it
                .map(GrantedAuthority::getAuthority)
                .none { it == ANONYMOUS }
        }
}

/**
 * Checks if the current user has any of the authorities.
 *
 * @param authorities the authorities to check.
 * @return true if the current user has any of the authorities, false otherwise.
 */
fun hasCurrentUserAnyOfAuthorities(vararg authorities: String): Mono<Boolean> {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(Authentication::getAuthorities)
        .map {
            it
                .map(GrantedAuthority::getAuthority)
                .any { authorities.contains(it) }
        }
}

/**
 * Checks if the current user has none of the authorities.
 *
 * @param authorities the authorities to check.
 * @return true if the current user has none of the authorities, false otherwise.
 */
fun hasCurrentUserNoneOfAuthorities(vararg authorities: String): Mono<Boolean> {
    return hasCurrentUserAnyOfAuthorities(*authorities).map { !it }
}

/**
 * Checks if the current user has a specific authority.
 *
 * @param authority the authority to check.
 * @return true if the current user has the authority, false otherwise.
 */
fun hasCurrentUserThisAuthority(authority: String): Mono<Boolean> {
    return hasCurrentUserAnyOfAuthorities(authority)
}
