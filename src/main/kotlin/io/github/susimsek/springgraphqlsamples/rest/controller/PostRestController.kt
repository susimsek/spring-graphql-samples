package io.github.susimsek.springgraphqlsamples.rest.controller

import io.github.susimsek.springgraphqlsamples.graphql.input.AddPostInput
import io.github.susimsek.springgraphqlsamples.graphql.type.PostPayload
import io.github.susimsek.springgraphqlsamples.service.PostService
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1")
class PostRestController(
    private val postService: PostService
) {

    @GetMapping("/posts/{postId}")
    suspend fun getPost(@PathVariable @Size(min = 36, max = 36) postId: String): PostPayload {
        return postService.getPost(postId)
    }

    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasRole('ASD')")
    suspend fun deletePost(@PathVariable @Size(min = 36, max = 36) postId: String): ResponseEntity<Unit> {
        postService.deletePost(postId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/posts")
    @PreAuthorize("hasRole('ADMIN')")
    suspend fun createPost(
        @RequestBody @Valid
        input: AddPostInput,
        locale: Locale
    ): PostPayload {
        return postService.createPost(input, locale)
    }
}
