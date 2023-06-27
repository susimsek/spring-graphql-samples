package io.github.susimsek.springgraphqlsamples.exception.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.susimsek.springgraphqlsamples.exception.model.Problem
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

object WebExceptionUtils {

    @Suppress("kotlin:S6508")
    fun setHttpResponse(
        exchange: ServerWebExchange,
        entity: Problem,
        mapper: ObjectMapper
    ): Mono<Void> {
        val response = exchange.response
        response.setStatusCode(entity.status)
        return try {
            val buffer = response.bufferFactory()
                .wrap(mapper.writeValueAsBytes(entity))
            response.writeWith(Mono.just(buffer))
                .doOnError { DataBufferUtils.release(buffer) }
        } catch (ex: JsonProcessingException) {
            Mono.error(ex)
        }
    }

    fun buildResponseEntity(problem: Problem): Mono<ResponseEntity<Any>> {
        return Mono.just(
            ResponseEntity.status(problem.status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(problem)
        )
    }
}
