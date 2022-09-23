package io.github.susimsek.springgraphqlsamples.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("security.authentication.token")
@ConstructorBinding
data class TokenProperties(
    var tokenValidityInSeconds: Long = 1800L,
    var base64Secret: String =
        "NjhkOGZmNmY4NGQ1ODFiMjI5M2JjZTJmYTRlMWVjNmM2Nzk2YjFiZTc2Nzc3MjMxMTE4Njg2ZjlhNTRhNmE0YmZhMzI5MDczZjQyZTZ" +
                "lZDFlYzdhZGZkYTUzZGUyMTMyM2VkZWRmNTEwZGQzNzc1YzIwMGRmYTFiNzAwNmI4Njc="
)
