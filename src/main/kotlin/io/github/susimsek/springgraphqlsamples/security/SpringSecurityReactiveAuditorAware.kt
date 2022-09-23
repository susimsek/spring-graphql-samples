package io.github.susimsek.springgraphqlsamples.security

import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SpringSecurityReactiveAuditorAware : ReactiveAuditorAware<String> {
    override fun getCurrentAuditor(): Mono<String> = getCurrentUserLogin()
}
