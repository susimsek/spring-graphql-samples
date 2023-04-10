package io.github.susimsek.springgraphqlsamples.service.chatgpt.payload

import com.fasterxml.jackson.annotation.JsonValue

enum class ImageSize(val size: String) {
    SMALL("256x256"),
    MEDIUM("512x512"),
    LARGE("1024x1024");

    @JsonValue
    fun toValue() = size
}
