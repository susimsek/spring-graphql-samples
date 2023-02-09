package io.github.susimsek.springgraphqlsamples.service.chatgpt

import kotlinx.coroutines.reactive.awaitSingle

class ChatGptService(
    private val chatGptClient: ChatGptClient,
    private val chatGptProperties: ChatGptProperties
) {

    suspend fun sendMessage(message: String): TextCompletion {
        val request = ChatGptRequest(
            model = chatGptProperties.model,
            temperature = chatGptProperties.temperature,
            maxTokens = chatGptProperties.maxTokens,
            prompt = message
        )
        val token = "Bearer ${chatGptProperties.secretKey}"

        return chatGptClient.createCompletion(
            token,
            request
        ).awaitSingle()
    }
}
