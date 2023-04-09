package io.github.susimsek.springgraphqlsamples.service.chatgpt.payload

data class ImageRequest(
    val prompt: String,
    val n: Int,
    val size: String
)