package io.github.susimsek.springgraphqlsamples.service.chatgpt

import io.github.susimsek.springgraphqlsamples.graphql.input.CreateImageInput
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.codec.multipart.FilePart

class ChatGptService(
    private val chatGptClient: ChatGptClient,
    private val chatGptProperties: ChatGptProperties
) {

    suspend fun chat(message: String): TextCompletion {
        val request = ChatGptRequest(
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

    suspend fun createImage(input: CreateImageInput): CreateImagePayload {
        val request = CreateImageRequest(
            prompt = input.prompt,
            n = chatGptProperties.image.number,
            size = chatGptProperties.image.size
        )
        return chatGptClient.createImage(
            request
        ).awaitSingle()
    }
}
