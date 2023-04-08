package io.github.susimsek.springgraphqlsamples.service.chatgpt.payload

import com.fasterxml.jackson.annotation.JsonProperty

data class CompletionChoice(
    val text: String,
    val index: Int,
    val logprobs: Any?,
    @field:JsonProperty("finish_reason")
    val finishReason: String
)
