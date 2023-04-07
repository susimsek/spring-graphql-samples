package io.github.susimsek.springgraphqlsamples.service.chatgpt

data class CreateImageRequest(
    val prompt: String,
    val n: Int,
    val size: String
)