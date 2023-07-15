package io.github.susimsek.springgraphqlsamples.config

import io.github.susimsek.springgraphqlsamples.exception.handler.ReactiveSecurityExceptionResolver
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

@TestConfiguration(proxyBeanMethods = false)
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(useAuthorizationManager = false)
class TestSecurityConfig {

    @Bean
    @Suppress("LongParameterList")
    fun springSecurityFilterChain(
        http: ServerHttpSecurity,
        securityExceptionResolver: ReactiveSecurityExceptionResolver
    ): SecurityWebFilterChain {
        // @formatter:off
        http
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .exceptionHandling { exceptionHandling ->
                exceptionHandling
                    .authenticationEntryPoint(securityExceptionResolver)
                    .accessDeniedHandler(securityExceptionResolver)
            }
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .logout(ServerHttpSecurity.LogoutSpec::disable)
            .authorizeExchange { auth ->
                auth
                    .pathMatchers(
                        "/graphiql",
                        "/graphql",
                        "/subscriptions",
                        "/api/v1/auth/**",
                        "/api/v1/transcription",
                        "/api/v1/translation",
                        "/redirect"
                    )
                    .permitAll()
                    .anyExchange().authenticated()
            }
        // @formatter:on
        return http.build()
    }
}
