package io.github.susimsek.springgraphqlsamples.service.chatgpt

data class CreateImagePayload(
    val created: Int,
    val data: List<Link>
)
