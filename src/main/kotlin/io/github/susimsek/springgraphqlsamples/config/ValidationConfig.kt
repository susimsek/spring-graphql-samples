package io.github.susimsek.springgraphqlsamples.config


import jakarta.validation.ClockProvider
import jakarta.validation.ParameterNameProvider
import org.hibernate.validator.internal.engine.DefaultClockProvider
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.validation.MessageInterpolatorFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.context.annotation.Role
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.core.KotlinReflectionParameterNameDiscoverer
import org.springframework.core.ParameterNameDiscoverer
import org.springframework.core.PrioritizedParameterNameDiscoverer
import org.springframework.core.StandardReflectionParameterNameDiscoverer
import org.springframework.http.server.reactive.HttpHandler
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.adapter.HttpWebHandlerAdapter
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import kotlin.reflect.jvm.kotlinFunction

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ImportRuntimeHints(ValidationConfig.GraphQlRuntimeHints::class)
class ValidationConfig {
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    fun defaultValidator(): LocalValidatorFactoryBean {
        val factoryBean = KotlinCoroutinesLocalValidatorFactoryBean()
        factoryBean.messageInterpolator = MessageInterpolatorFactory().getObject()
        return factoryBean
    }

    @Bean
    fun httpHandler(applicationContext: ApplicationContext): HttpHandler {
        val delegate = WebHttpHandlerBuilder
            .applicationContext(applicationContext).build()
        return object : HttpWebHandlerAdapter((delegate as HttpWebHandlerAdapter)) {
            override fun createExchange(
                request: ServerHttpRequest,
                response: ServerHttpResponse
            ): ServerWebExchange {
                val serverWebExchange = super
                    .createExchange(request, response)
                val localeContext = serverWebExchange.localeContext
                LocaleContextHolder.setLocaleContext(localeContext)
                return serverWebExchange
            }
        }
    }

    internal class GraphQlRuntimeHints : RuntimeHintsRegistrar {
        override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
            hints.resources().registerResourceBundle("i18n.messages")
        }
    }
}

class KotlinCoroutinesLocalValidatorFactoryBean : LocalValidatorFactoryBean() {
    override fun getClockProvider(): ClockProvider = DefaultClockProvider.INSTANCE

    override fun postProcessConfiguration(configuration: jakarta.validation.Configuration<*>) {
        super.postProcessConfiguration(configuration)

        val discoverer = PrioritizedParameterNameDiscoverer()
        discoverer.addDiscoverer(SuspendAwareKotlinParameterNameDiscoverer())
        discoverer.addDiscoverer(StandardReflectionParameterNameDiscoverer())

        val defaultProvider = configuration.defaultParameterNameProvider
        configuration.parameterNameProvider(object : ParameterNameProvider {
            override fun getParameterNames(constructor: Constructor<*>): List<String> {
                val paramNames: Array<String>? = discoverer.getParameterNames(constructor)
                return paramNames?.toList() ?: defaultProvider.getParameterNames(constructor)
            }

            override fun getParameterNames(method: Method): List<String> {
                val paramNames: Array<String>? = discoverer.getParameterNames(method)
                return paramNames?.toList() ?: defaultProvider.getParameterNames(method)
            }
        })
    }
}

class SuspendAwareKotlinParameterNameDiscoverer : ParameterNameDiscoverer {

    private val defaultProvider = KotlinReflectionParameterNameDiscoverer()

    override fun getParameterNames(constructor: Constructor<*>): Array<String>? =
        defaultProvider.getParameterNames(constructor)

    override fun getParameterNames(method: Method): Array<String>? {
        val defaultNames = defaultProvider.getParameterNames(method) ?: return null
        val function = method.kotlinFunction
        return if (function != null && function.isSuspend) {
            defaultNames + ""
        } else defaultNames
    }
}
