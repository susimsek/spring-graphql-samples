package io.github.susimsek.springgraphqlsamples.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.CollectionUtils
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CorsProperties::class)
class CorsConfig {
    @Bean
    fun corsFilter(corsProperties: CorsProperties): CorsWebFilter {
        val source = UrlBasedCorsConfigurationSource()
        if (!CollectionUtils.isEmpty(corsProperties.allowedOrigins)) {
            val config = CorsConfiguration()
            config.allowedOrigins = corsProperties.allowedOrigins
            config.allowedMethods = corsProperties.allowedMethods
            config.allowedHeaders = corsProperties.allowedHeaders
            config.exposedHeaders = corsProperties.exposedHeaders
            config.allowCredentials = corsProperties.allowCredentials
            config.maxAge = corsProperties.maxAge

            source.apply {
                registerCorsConfiguration("/graphql", config)
            }
        }
        return CorsWebFilter(source)
    }
}
