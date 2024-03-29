package io.github.susimsek.springgraphqlsamples.service.chatgpt

import io.github.susimsek.springgraphqlsamples.graphql.input.ImageInput
import io.github.susimsek.springgraphqlsamples.service.chatgpt.payload.ImagePayload
import io.github.susimsek.springgraphqlsamples.service.chatgpt.payload.ImageRequest
import io.github.susimsek.springgraphqlsamples.service.chatgpt.payload.TextCompletionPayload
import io.github.susimsek.springgraphqlsamples.service.chatgpt.payload.TextCompletionRequest
import io.github.susimsek.springgraphqlsamples.service.chatgpt.payload.TextModerationPayload
import io.github.susimsek.springgraphqlsamples.service.chatgpt.payload.TextModerationRequest
import io.github.susimsek.springgraphqlsamples.service.chatgpt.payload.TranscriptionPayload
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.codec.multipart.FilePart

class ChatGptService(
    private val chatGptClient: ChatGptClient,
    private val chatGptProperties: ChatGptProperties
) {

    suspend fun createCompletion(message: String): TextCompletionPayload {
        val request = TextCompletionRequest(
            model = chatGptProperties.gptModel,
            temperature = chatGptProperties.temperature,
            maxTokens = chatGptProperties.maxTokens,
            prompt = message
        )
        return chatGptClient.createCompletion(
            request
        ).awaitSingle()
    }

    suspend fun createTranscription(audio: FilePart): TranscriptionPayload {
        return chatGptClient.createTranscription(
            chatGptProperties.audioModel,
            audio
        ).awaitSingle()
    }

    suspend fun createTranslation(audio: FilePart): TranscriptionPayload {
        return chatGptClient.createTranslation(
            chatGptProperties.audioModel,
            audio
        ).awaitSingle()
    }

    suspend fun createImage(input: ImageInput): ImagePayload {
        val request = ImageRequest(
            prompt = input.prompt,
            n = chatGptProperties.image.number,
            size = chatGptProperties.image.size
        )
        return chatGptClient.createImage(
            request
        ).awaitSingle()
    }

    suspend fun createModeration(input: String): TextModerationPayload {
        val request = TextModerationRequest(
            input = input,
            model = chatGptProperties.moderationModel
        )
        return chatGptClient.createModeration(request)
            .awaitSingle()
    }
}
