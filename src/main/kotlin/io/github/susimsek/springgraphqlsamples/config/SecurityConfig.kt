package io.github.susimsek.springgraphqlsamples.config

// import org.springframework.messaging.rsocket.RSocketStrategies
// import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
// import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver
// import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import io.github.susimsek.springgraphqlsamples.security.WebSocketAuthenticationInterceptor
import io.github.susimsek.springgraphqlsamples.security.cipher.RSAKeyUtils
import io.github.susimsek.springgraphqlsamples.security.cipher.SecurityCipher
import io.github.susimsek.springgraphqlsamples.security.jwt.AUTHORITIES_KEY
import io.github.susimsek.springgraphqlsamples.security.jwt.JwtDecoder
import io.github.susimsek.springgraphqlsamples.security.jwt.TokenProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers
import reactor.core.publisher.Mono
import java.security.KeyPair
import java.security.interfaces.RSAPublicKey

@Configuration(proxyBeanMethods = false)
@EnableWebFluxSecurity
// @EnableRSocketSecurity
@EnableReactiveMethodSecurity
@EnableConfigurationProperties(SecurityMatcherProperties::class)
class SecurityConfig(
    private val userDetailsService: ReactiveUserDetailsService,
    private val securityMatcherProperties: SecurityMatcherProperties
) {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    /*
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
                      jwtDecoder: ReactiveJwtDecoder
    ): PayloadSocketAcceptorInterceptor {
        security.authorizePayload { authorize ->
            authorize
                    .anyRequest().authenticated()
                    .anyExchange().permitAll()
                }.jwt { jwtSpec -> jwtSpec.authenticationManager(jwtReactiveAuthenticationManager(jwtDecoder)) }
        return security.build()
    }

     */

    @Bean
    fun keyPair(tokenProperties: TokenProperties): KeyPair {
        return KeyPair(
            RSAKeyUtils.generatePublicKey(tokenProperties.publicKey),
            RSAKeyUtils.generatePrivateKey(tokenProperties.privateKey)
        )
    }

    @Bean
    fun jwtDecoder(
        keyPair: KeyPair,
        securityCipher: SecurityCipher
    ): ReactiveJwtDecoder {
        return JwtDecoder(keyPair.public as RSAPublicKey, securityCipher)
    }

    @Bean
    fun jwtEncoder(keyPair: KeyPair): JwtEncoder {
        val jwk = RSAKey.Builder(keyPair.public as RSAPublicKey)
            .privateKey(keyPair.private)
            .build()
        val jwks: JWKSource<SecurityContext> = ImmutableJWKSet(JWKSet(jwk))
        return NimbusJwtEncoder(jwks)
    }

    @Bean
    fun graphqlWsAuthenticationInterceptor(decoder: ReactiveJwtDecoder,
                                         jwtAuthenticationConverter: Converter<Jwt, Mono<AbstractAuthenticationToken>>
    ): WebSocketAuthenticationInterceptor {
        val manager = JwtReactiveAuthenticationManager(decoder)
        manager.setJwtAuthenticationConverter(jwtAuthenticationConverter)
        return WebSocketAuthenticationInterceptor(manager)
    }

    @Bean
    fun jwtAuthenticationConverter(): Converter<Jwt, Mono<AbstractAuthenticationToken>> {
        val authoritiesConverter = JwtGrantedAuthoritiesConverter()
        authoritiesConverter.setAuthoritiesClaimName(AUTHORITIES_KEY)
        authoritiesConverter.setAuthorityPrefix("")
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter)
        return ReactiveJwtAuthenticationConverterAdapter(converter)
    }

    @Bean
    fun reactiveAuthenticationManager() =
        UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService).apply {
            setPasswordEncoder(passwordEncoder())
        }

    @Bean
    fun springSecurityFilterChain(
        http: ServerHttpSecurity,
        jwtAuthenticationConverter: Converter<Jwt, Mono<AbstractAuthenticationToken>>): SecurityWebFilterChain {
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
            .cors().and()
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic().disable()
            .logout().disable()
            .headers()
            .referrerPolicy(ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            .and()
            .frameOptions().disable()
            .and()
            .authorizeExchange()
            .pathMatchers(*securityMatcherProperties.permitAllPatterns.toTypedArray()).permitAll()
            .and()
            .oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(jwtAuthenticationConverter)
        // @formatter:on
        return http.build()
    }
}
