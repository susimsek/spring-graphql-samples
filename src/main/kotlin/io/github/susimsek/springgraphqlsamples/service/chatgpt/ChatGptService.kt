package io.github.susimsek.springgraphqlsamples.service.chatgpt

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
}
