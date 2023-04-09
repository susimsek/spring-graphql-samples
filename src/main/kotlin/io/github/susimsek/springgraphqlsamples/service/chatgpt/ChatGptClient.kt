package io.github.susimsek.springgraphqlsamples.service.chatgpt

import io.github.susimsek.springgraphqlsamples.service.chatgpt.payload.ImagePayload
import io.github.susimsek.springgraphqlsamples.service.chatgpt.payload.ImageRequest
import io.github.susimsek.springgraphqlsamples.service.chatgpt.payload.TextCompletionPayload
import io.github.susimsek.springgraphqlsamples.service.chatgpt.payload.TextCompletionRequest
import io.github.susimsek.springgraphqlsamples.service.chatgpt.payload.TextModerationPayload
import io.github.susimsek.springgraphqlsamples.service.chatgpt.payload.TextModerationRequest
import io.github.susimsek.springgraphqlsamples.service.chatgpt.payload.TranscriptionPayload
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
        @RequestBody completion: TextCompletionRequest
    ): Mono<TextCompletionPayload>

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
        @RequestBody image: ImageRequest
    ): Mono<ImagePayload>

    @PostExchange("/moderations")
    fun createModeration(
        @RequestBody moderation: TextModerationRequest
    ): Mono<TextModerationPayload>
}
