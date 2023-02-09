package io.github.susimsek.springgraphqlsamples.service.chatgpt

import com.fasterxml.jackson.annotation.JsonProperty

data class Usage(

    @field:JsonProperty("prompt_tokens")
    val promptTokens: Int,

    @field:JsonProperty("completion_tokens")
    val completionTokens: Int,

    @field:JsonProperty("total_tokens")
    val totalTokens: Int
)
