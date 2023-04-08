package io.github.susimsek.springgraphqlsamples.service.chatgpt.payload

data class CreateImagePayload(
    val created: Int,
    val data: List<Link>
)
