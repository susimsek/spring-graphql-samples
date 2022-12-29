package io.github.susimsek.springgraphqlsamples.security.recaptcha

import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import reactor.core.publisher.Mono


@HttpExchange
interface RecaptchaClient{
    @PostExchange
    fun verifyResponse(@RequestParam secret :String,
                      @RequestParam("response") recaptchaToken :String): Mono<RecaptchaResponse>
}
