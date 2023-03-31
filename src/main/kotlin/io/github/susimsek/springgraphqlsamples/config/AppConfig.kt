package io.github.susimsek.springgraphqlsamples.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AppProperties::class)
class AppConfig {
    @Bean
    fun clock(): Clock {
        return Clock.systemDefaultZone()
    }
}
