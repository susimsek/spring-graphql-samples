package io.github.susimsek.springgraphqlsamples.service.chatgpt.payload

data class TextModerationRequest(
    val input: String,
    val model: String
)
