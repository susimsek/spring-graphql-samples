package io.github.susimsek.springgraphqlsamples.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.convert.converter.Converter
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor
import reactor.core.publisher.Mono

@Configuration(proxyBeanMethods = false)
@EnableRSocketSecurity
@Profile("rsocket")
class RSocketSecurityConfig {

    @Bean
    fun messageHandler(strategies: RSocketStrategies): RSocketMessageHandler {
        val handler = RSocketMessageHandler()
        handler.argumentResolverConfigurer.addCustomResolver(AuthenticationPrincipalArgumentResolver())
        handler.rSocketStrategies = strategies
        return handler
    }

    @Bean
    fun authorization(
        security: RSocketSecurity,
        decoder: ReactiveJwtDecoder,
        jwtAuthenticationConverter: Converter<Jwt, Mono<AbstractAuthenticationToken>>
    ): PayloadSocketAcceptorInterceptor {
        val manager = JwtReactiveAuthenticationManager(decoder)
        manager.setJwtAuthenticationConverter(jwtAuthenticationConverter)
        security.authorizePayload { authorize ->
            authorize
                .anyRequest().authenticated()
                .anyExchange().permitAll()
        }.jwt { jwtSpec -> jwtSpec.authenticationManager(manager) }
        return security.build()
    }
}
