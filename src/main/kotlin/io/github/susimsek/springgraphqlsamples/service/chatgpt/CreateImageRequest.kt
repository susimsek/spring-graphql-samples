package io.github.susimsek.springgraphqlsamples.service.chatgpt

data class CreateImageRequest(
    val prompt: String,
    val n: Int = 1,
    val size: String = "1024x1024"
)