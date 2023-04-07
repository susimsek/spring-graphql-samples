package io.github.susimsek.springgraphqlsamples.service.chatgpt

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@ConfigurationProperties("chatgpt")
@Validated
data class ChatGptProperties(
    @field:NotBlank
    var secretKey: String,

    @field:NotBlank
    var baseUrl: String,

    @field:NotBlank
    val gptModel: String,

    @field:NotBlank
    val audioModel: String,

    @field:DecimalMin("0.0")
    @field:DecimalMax("2.0")
    val temperature: Float,

    @field:Max(4000)
    val maxTokens: Int
)
