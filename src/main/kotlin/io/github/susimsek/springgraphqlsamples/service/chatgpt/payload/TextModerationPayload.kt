package io.github.susimsek.springgraphqlsamples.service.chatgpt.payload

data class TextModerationPayload(
    val id: String,
    val model: String,
    val results: List<TextModeration>
)
