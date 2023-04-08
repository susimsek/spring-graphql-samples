package io.github.susimsek.springgraphqlsamples.service.chatgpt.payload

import com.fasterxml.jackson.annotation.JsonProperty

data class TextModeration(
    @field:JsonProperty("category_scores")
    val categoryScores: ModerationCategoryScore,
    val flagged: Boolean,
    val categories: ModerationCategory
)
