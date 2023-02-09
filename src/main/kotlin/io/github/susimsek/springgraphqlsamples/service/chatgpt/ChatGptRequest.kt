package io.github.susimsek.springgraphqlsamples.service.chatgpt

import com.fasterxml.jackson.annotation.JsonProperty

data class ChatGptRequest(
    val model: String,
    val prompt: String,
    val temperature: Float,
    @field:JsonProperty("max_tokens")
    val maxTokens: Int
)
