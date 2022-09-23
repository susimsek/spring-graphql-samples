package io.github.susimsek.springgraphqlsamples.config

import io.github.susimsek.springgraphqlsamples.security.cipher.SecurityCipher
import io.github.susimsek.springgraphqlsamples.security.jwt.JWTFilter
import io.github.susimsek.springgraphqlsamples.security.jwt.JwtDecoder
import io.github.susimsek.springgraphqlsamples.security.jwt.TokenProperties
import io.github.susimsek.springgraphqlsamples.security.jwt.TokenProvider
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers


@EnableWebFluxSecurity
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
@EnableConfigurationProperties(SecurityMatcherProperties::class)
class SecurityConfig(
    private val userDetailsService: ReactiveUserDetailsService,
    private val tokenProvider: TokenProvider,
    private val securityCipher: SecurityCipher,
    private val securityMatcherProperties: SecurityMatcherProperties
) {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun messageHandler(strategies: RSocketStrategies): RSocketMessageHandler {
        val handler = RSocketMessageHandler()
        handler.argumentResolverConfigurer.addCustomResolver(AuthenticationPrincipalArgumentResolver())
        handler.rSocketStrategies = strategies
        return handler
    }

    @Bean
    fun authorization(security: RSocketSecurity,
                      jwtDecoder: ReactiveJwtDecoder): PayloadSocketAcceptorInterceptor {
        security.authorizePayload { authorize -> authorize
                    .anyRequest().authenticated()
                    .anyExchange().permitAll()
                } .jwt(withDefaults())
        return security.build()
    }

    @Bean
    fun jwtDecoder(
        tokenProperties: TokenProperties,
        securityCipher: SecurityCipher): ReactiveJwtDecoder {
        return JwtDecoder(tokenProperties.base64Secret, securityCipher)
    }

    @Bean
    fun reactiveAuthenticationManager() =
        UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService).apply {
            setPasswordEncoder(passwordEncoder())
        }


    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        // @formatter:off
        http
            .securityMatcher(
                NegatedServerWebExchangeMatcher(
                    OrServerWebExchangeMatcher(
                        pathMatchers(*securityMatcherProperties.ignorePatterns.toTypedArray()),
                        pathMatchers(HttpMethod.OPTIONS, "/**")
                    )
                )
            )
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic().disable()
            .logout().disable()
            .addFilterAt(JWTFilter(tokenProvider, securityCipher), SecurityWebFiltersOrder.HTTP_BASIC)
            .authenticationManager(reactiveAuthenticationManager())
            .headers()
            .referrerPolicy(ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            .and()
            .frameOptions().disable()
            .and()
            .authorizeExchange()
            .pathMatchers(*securityMatcherProperties.permitAllPatterns.toTypedArray()).permitAll()
            .anyExchange().authenticated()
        // @formatter:on
        return http.build()
    }
}
