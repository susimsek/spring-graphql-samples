package io.github.susimsek.springgraphqlsamples.service.chatgpt.payload

@Suppress("ConstructorParameterNaming")
data class TextCompletionPayload(
    val id: String,
    val `object`: String,
    val created: Int,
    val model: String,
    val choices: List<CompletionChoice>,
    val usage: Usage
)
