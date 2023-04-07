package io.github.susimsek.springgraphqlsamples.service.chatgpt

import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import reactor.core.publisher.Mono

@HttpExchange
interface ChatGptClient {

    @PostExchange("/completions")
    fun createCompletion(
        @RequestBody completion: ChatGptRequest
    ): Mono<TextCompletion>

    @PostExchange("/audio/transcriptions")
    fun createTranscription(
        @RequestPart model: String,
        @RequestPart file: FilePart
    ): Mono<TranscriptionPayload>

    @PostExchange("/audio/translations")
    fun createTranslation(
        @RequestPart model: String,
        @RequestPart file: FilePart
    ): Mono<TranscriptionPayload>


    @PostExchange("/images/generations")
    fun createImage(
        @RequestBody image: CreateImageRequest
    ): Mono<CreateImagePayload>
}
