package io.github.susimsek.springgraphqlsamples.service.chatgpt

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import reactor.core.publisher.Mono

@HttpExchange
interface ChatGptClient {

    @PostExchange("/completions")
    fun createCompletion(
        @RequestHeader("Authorization") token: String,
        @RequestBody completion: ChatGptRequest
    ): Mono<TextCompletion>
}
