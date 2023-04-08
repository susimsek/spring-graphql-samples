package io.github.susimsek.springgraphqlsamples.service.chatgpt.payload

import com.fasterxml.jackson.annotation.JsonProperty

data class ModerationCategory(
    @field:JsonProperty("hate/threatening")
    val hateThreatening: Boolean,
    @field:JsonProperty("sexual/minors")
    val sexualMinors: Boolean,
    val hate: Boolean,
    @field:JsonProperty("self-harm")
    val selfHarm: Boolean,
    val sexual: Boolean,
    @field:JsonProperty("violence/graphic")
    val violenceGraphic: Boolean,
    val violence: Boolean
)
