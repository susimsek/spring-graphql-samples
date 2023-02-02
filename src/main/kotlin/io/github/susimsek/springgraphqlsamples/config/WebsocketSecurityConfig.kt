package io.github.susimsek.springgraphqlsamples.config

import io.github.susimsek.springgraphqlsamples.security.GraphQlWsAuthenticationInterceptor
import io.github.susimsek.springgraphqlsamples.security.jwt.GraphQlTokenCookieInterceptor
import io.github.susimsek.springgraphqlsamples.security.jwt.TokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager
import reactor.core.publisher.Mono

@Configuration(proxyBeanMethods = false)
@Profile("websocket")
class WebsocketSecurityConfig {

    @Bean
    fun graphQlWsAuthenticationInterceptor(
        decoder: ReactiveJwtDecoder,
        jwtAuthenticationConverter: Converter<Jwt, Mono<AbstractAuthenticationToken>>
    ): GraphQlWsAuthenticationInterceptor {
        val manager = JwtReactiveAuthenticationManager(decoder)
        manager.setJwtAuthenticationConverter(jwtAuthenticationConverter)
        return GraphQlWsAuthenticationInterceptor(manager)
    }

    @Bean
    fun graphQlTokenCookieInterceptor(tokenProvider: TokenProvider): GraphQlTokenCookieInterceptor {
        return GraphQlTokenCookieInterceptor(tokenProvider)
    }
}
