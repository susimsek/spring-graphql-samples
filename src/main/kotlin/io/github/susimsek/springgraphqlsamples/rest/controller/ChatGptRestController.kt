package io.github.susimsek.springgraphqlsamples.rest.controller

import io.github.susimsek.springgraphqlsamples.service.chatgpt.ChatGptService
import io.github.susimsek.springgraphqlsamples.service.chatgpt.TranscriptionPayload
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1")
@PreAuthorize("isAuthenticated()")
class ChatGptRestController(
    private val chatGptService: ChatGptService
) {

    @PostMapping("/transcription")
    suspend fun createTranscription(@RequestPart audio: FilePart): TranscriptionPayload {
        return chatGptService.createTranscription(audio)
    }
}
