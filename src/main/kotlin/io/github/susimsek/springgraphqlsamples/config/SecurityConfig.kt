package io.github.susimsek.springgraphqlsamples.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import io.github.susimsek.springgraphqlsamples.exception.handler.ReactiveSecurityExceptionResolver
import io.github.susimsek.springgraphqlsamples.security.cipher.RSAKeyUtils
import io.github.susimsek.springgraphqlsamples.security.cipher.SecurityCipher
import io.github.susimsek.springgraphqlsamples.security.jwt.AUTHORITIES_KEY
import io.github.susimsek.springgraphqlsamples.security.jwt.JwtDecoder
import io.github.susimsek.springgraphqlsamples.security.jwt.TokenAuthenticationConverter
import io.github.susimsek.springgraphqlsamples.security.jwt.TokenProvider
import io.github.susimsek.springgraphqlsamples.security.recaptcha.RecaptchaFilter
import io.github.susimsek.springgraphqlsamples.security.recaptcha.RecaptchaService
import io.github.susimsek.springgraphqlsamples.security.xss.XSSFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import reactor.core.publisher.Mono
import java.security.KeyPair
import java.security.interfaces.RSAPublicKey

@Configuration(proxyBeanMethods = false)
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(useAuthorizationManager = false)
@EnableConfigurationProperties(SecurityProperties::class)
class SecurityConfig {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun keyPair(securityProperties: SecurityProperties): KeyPair {
        return KeyPair(
            RSAKeyUtils.generatePublicKey(securityProperties.authentication.token.publicKey),
            RSAKeyUtils.generatePrivateKey(securityProperties.authentication.token.privateKey)
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
    fun tokenProvider(
        securityProperties: SecurityProperties,
        jwtEncoder: JwtEncoder
    ): TokenProvider {
        return TokenProvider(securityProperties.authentication.token, jwtEncoder)
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
    fun bearerTokenConverter(securityProperties: SecurityProperties): ServerAuthenticationConverter {
        return TokenAuthenticationConverter(securityProperties.authentication.token)
    }

    @Bean
    fun reactiveAuthenticationManager(userDetailsService: ReactiveUserDetailsService) =
        UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService).apply {
            setPasswordEncoder(passwordEncoder())
        }

    @Bean
    @Suppress("LongParameterList")
    fun springSecurityFilterChain(
        http: ServerHttpSecurity,
        securityProperties: SecurityProperties,
        jwtAuthenticationConverter: Converter<Jwt, Mono<AbstractAuthenticationToken>>,
        bearerTokenConverter: ServerAuthenticationConverter,
        securityExceptionResolver: ReactiveSecurityExceptionResolver,
        recaptchaService: RecaptchaService
    ): SecurityWebFilterChain {
        // @formatter:off
        http
            .securityMatcher(
                NegatedServerWebExchangeMatcher(
                    OrServerWebExchangeMatcher(
                        ServerWebExchangeMatchers.pathMatchers(
                            *securityProperties.authentication.securityMatcher.ignorePatterns.toTypedArray()
                        ),
                        ServerWebExchangeMatchers.pathMatchers(HttpMethod.OPTIONS, "/**")
                    )
                )
            )
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .exceptionHandling { exceptionHandling ->
                exceptionHandling
                    .authenticationEntryPoint(securityExceptionResolver)
                    .accessDeniedHandler(securityExceptionResolver)
            }
            .cors(Customizer.withDefaults())
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .logout(ServerHttpSecurity.LogoutSpec::disable)
            .headers { headers ->
                headers
                    .frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable)
                    .contentSecurityPolicy { contentSecurityPolicy ->
                        contentSecurityPolicy.policyDirectives(securityProperties.contentSecurityPolicy)
                    }
            }
            .authorizeExchange { auth ->
                auth
                    .pathMatchers(*securityProperties.authentication.securityMatcher.permitAllPatterns.toTypedArray())
                    .permitAll()
                    .anyExchange().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2
                    .jwt { jwt ->
                        jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
                    }
                    .bearerTokenConverter(bearerTokenConverter)
                    .authenticationEntryPoint(securityExceptionResolver)
                    .accessDeniedHandler(securityExceptionResolver)
            }
            .addFilterAt(XSSFilter(), SecurityWebFiltersOrder.HTTP_HEADERS_WRITER)
            .addFilterBefore(RecaptchaFilter(recaptchaService), SecurityWebFiltersOrder.AUTHENTICATION)
        // @formatter:on
        return http.build()
    }
}
