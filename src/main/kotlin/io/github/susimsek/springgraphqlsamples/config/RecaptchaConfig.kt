package io.github.susimsek.springgraphqlsamples.config

import io.github.susimsek.springgraphqlsamples.security.recaptcha.GraphQlRecaptchaHeaderInterceptor
import io.github.susimsek.springgraphqlsamples.security.recaptcha.RecaptchaClient
import io.github.susimsek.springgraphqlsamples.security.recaptcha.RecaptchaProperties
import io.github.susimsek.springgraphqlsamples.security.recaptcha.RecaptchaService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RecaptchaProperties::class)
class RecaptchaConfig {

    @Bean
    fun recaptchaClient(
        webClientBuilder: WebClient.Builder,
        recaptchaProperties: RecaptchaProperties
    ): RecaptchaClient {
        val webClient = webClientBuilder.baseUrl(recaptchaProperties.verifyUrl)
            .build()
        val httpServiceProxyFactory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient))
            .build()
        return httpServiceProxyFactory.createClient(RecaptchaClient::class.java)
    }

    @Bean
    fun recaptchaService(
        recaptchaClient: RecaptchaClient,
        recaptchaProperties: RecaptchaProperties
    ): RecaptchaService {
        return RecaptchaService(recaptchaClient, recaptchaProperties)
    }

    @Bean
    fun graphQlRecaptchaInterceptor(): GraphQlRecaptchaHeaderInterceptor {
        return GraphQlRecaptchaHeaderInterceptor()
    }
}
