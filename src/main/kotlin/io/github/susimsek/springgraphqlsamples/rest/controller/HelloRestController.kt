package io.github.susimsek.springgraphqlsamples.rest.controller

import io.github.susimsek.springgraphqlsamples.graphql.type.Message
import io.github.susimsek.springgraphqlsamples.rest.payload.HelloRequest
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.net.URL
import java.time.LocalDate
import java.util.*

@Tag(name = "hello", description = "Sample API")
@RestController
@RequestMapping("/api/v1/nonsecure")
class HelloRestController {

    @PostMapping("/hello")
    suspend fun hello(@RequestBody hello: HelloRequest): Message {
        return Mono.just(
            Message(
                id = UUID.randomUUID(),
                url = URL("https://www.w3.org/Addressing/URL/url-spec.txt"),
                content = mutableMapOf("sivas" to "58", "istanbul" to "43"),
                sentDate = LocalDate.now(),
                price = BigDecimal(30.82)
            )
        ).awaitSingle()
    }
}
