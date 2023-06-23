package io.github.susimsek.springgraphqlsamples.rest.controller

import io.github.susimsek.springgraphqlsamples.graphql.input.AddPostInput
import io.github.susimsek.springgraphqlsamples.graphql.type.PostPayload
import io.github.susimsek.springgraphqlsamples.service.PostService
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("isAuthenticated()")
class PostRestController(
    private val postService: PostService
) {

    @GetMapping("/posts/{postId}")
    suspend fun getPost(@PathVariable @Size(min = 36, max = 36) postId: String): PostPayload {
        return postService.getPost(postId)
    }

    @DeleteMapping("/posts/{postId}")
    suspend fun deletePost(@PathVariable @Size(min = 36, max = 36) postId: String): ResponseEntity<Unit> {
        postService.deletePost(postId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/posts")
    @PreAuthorize("hasRole('USER')")
    suspend fun createPost(
        @RequestBody @Valid
        input: AddPostInput,
        locale: Locale
    ): PostPayload {
        return postService.createPost(input, locale)
    }
}
