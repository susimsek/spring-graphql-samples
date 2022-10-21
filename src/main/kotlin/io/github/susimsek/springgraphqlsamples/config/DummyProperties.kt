package io.github.susimsek.springgraphqlsamples.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("dummy")
@ConstructorBinding
data class DummyProperties(
    var message: String = "this is a dummy message"
)
