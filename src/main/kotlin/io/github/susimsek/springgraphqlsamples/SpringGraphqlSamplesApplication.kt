package io.github.susimsek.springgraphqlsamples

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class SpringGraphqlSamplesApplication

fun main(args: Array<String>) {
    runApplication<SpringGraphqlSamplesApplication>(*args)
}
