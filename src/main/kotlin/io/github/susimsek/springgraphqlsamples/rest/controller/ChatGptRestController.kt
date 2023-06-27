package io.github.susimsek.springgraphqlsamples.rest.controller

import io.github.susimsek.springgraphqlsamples.service.chatgpt.ChatGptService
import io.github.susimsek.springgraphqlsamples.service.chatgpt.payload.TranscriptionPayload
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController


@Tag(name = "chatgpt", description = "ChatGPT API")
@RestController
@RequestMapping("/api/v1")
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearerAuth")
class ChatGptRestController(
    private val chatGptService: ChatGptService
) {

    @PostMapping("/transcription")
    suspend fun createTranscription(@RequestPart audio: FilePart): TranscriptionPayload {
        return chatGptService.createTranscription(audio)
    }

    @PostMapping("/translation")
    suspend fun createTranslation(@RequestPart audio: FilePart): TranscriptionPayload {
        return chatGptService.createTranslation(audio)
    }
}
