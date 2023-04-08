package io.github.susimsek.springgraphqlsamples.service.chatgpt.payload

import com.fasterxml.jackson.annotation.JsonProperty

data class ModerationCategoryScore(
    @field:JsonProperty("hate/threatening")
    val hateThreatening: Float,
    @field:JsonProperty("sexual/minors")
    val sexualMinors: Float,
    val hate: Float,
    @field:JsonProperty("self-harm")
    val selfHarm: Float,
    val sexual: Float,
    @field:JsonProperty("violence/graphic")
    val violenceGraphic: Float,
    val violence: Float
)
