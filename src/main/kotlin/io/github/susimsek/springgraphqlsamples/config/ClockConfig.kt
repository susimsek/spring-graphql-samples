package io.github.susimsek.springgraphqlsamples.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration(proxyBeanMethods = false)
class ClockConfig {
    @Bean
    fun clock(): Clock {
        return Clock.systemDefaultZone()
    }
}
