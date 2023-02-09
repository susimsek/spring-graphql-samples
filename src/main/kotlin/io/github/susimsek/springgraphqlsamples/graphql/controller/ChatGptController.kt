package io.github.susimsek.springgraphqlsamples.graphql.controller

import io.github.susimsek.springgraphqlsamples.graphql.input.TextCompletionInput
import io.github.susimsek.springgraphqlsamples.service.chatgpt.ChatGptService
import io.github.susimsek.springgraphqlsamples.service.chatgpt.TextCompletion
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller

@Controller
@PreAuthorize("isAuthenticated()")
class ChatGptController(
    private val chatGptService: ChatGptService
) {

    @MutationMapping
    suspend fun textCompletion(@Argument input: TextCompletionInput): TextCompletion {
        return chatGptService.sendMessage(input.prompt)
    }
}
