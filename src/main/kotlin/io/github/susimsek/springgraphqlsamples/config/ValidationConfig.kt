package io.github.susimsek.springgraphqlsamples.config

import jakarta.validation.ClockProvider
import jakarta.validation.ParameterNameProvider
import org.hibernate.validator.internal.engine.DefaultClockProvider
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.validation.MessageInterpolatorFactory
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Role
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.core.KotlinReflectionParameterNameDiscoverer
import org.springframework.core.LocalVariableTableParameterNameDiscoverer
import org.springframework.core.ParameterNameDiscoverer
import org.springframework.core.PrioritizedParameterNameDiscoverer
import org.springframework.core.StandardReflectionParameterNameDiscoverer
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import kotlin.reflect.jvm.kotlinFunction


@Configuration(proxyBeanMethods = false)
class ValidationAutoConfiguration {

    @Bean
    fun messageSource(): MessageSource {
        val messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setBasenames("classpath:i18n")
        messageSource.setDefaultEncoding("UTF-8")
        return messageSource
    }

    @Primary
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    fun validatorFactoryBean(messageSource: MessageSource): LocalValidatorFactoryBean {
        val factoryBean = KotlinCoroutinesLocalValidatorFactoryBean()
        factoryBean.messageInterpolator = MessageInterpolatorFactory().getObject()
        factoryBean.setValidationMessageSource(messageSource)
        return factoryBean
    }
}

class KotlinCoroutinesLocalValidatorFactoryBean : LocalValidatorFactoryBean() {
    override fun getClockProvider(): ClockProvider = DefaultClockProvider.INSTANCE

    override fun postProcessConfiguration(configuration: jakarta.validation.Configuration<*>) {
        super.postProcessConfiguration(configuration)

        val discoverer = PrioritizedParameterNameDiscoverer()
        discoverer.addDiscoverer(SuspendAwareKotlinParameterNameDiscoverer())
        discoverer.addDiscoverer(StandardReflectionParameterNameDiscoverer())
        discoverer.addDiscoverer(LocalVariableTableParameterNameDiscoverer())

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
