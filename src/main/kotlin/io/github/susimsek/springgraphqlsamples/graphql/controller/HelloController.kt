package io.github.susimsek.springgraphqlsamples.graphql.controller

import io.github.susimsek.springgraphqlsamples.graphql.type.Message
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import java.time.LocalDate


@Controller
class HelloController {

    @QueryMapping
    suspend fun hello(): Message {
        return Mono.just(
            Message(
                content =  mutableMapOf("sivas" to "58", "istanbul" to "43"),
                sentDate = LocalDate.now()
            )
        ).awaitSingle()
    }
}
