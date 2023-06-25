package io.github.susimsek.springgraphqlsamples.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.KotlinDetector
import org.springframework.core.io.ResourceLoader
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.util.ClassUtils

@Configuration(proxyBeanMethods = false)
class RedisConfig {

    @Bean
    fun reactiveRedisTemplate(
        factory: ReactiveRedisConnectionFactory,
        objectMapper: ObjectMapper,
        resourceLoader: ResourceLoader,
        appProperties: AppProperties,
    ): ReactiveRedisTemplate<String, Any> {
        val valueSerializer = valueRedisSerializer(
            resourceLoader,
            objectMapper,
            appProperties.cache.redis.serializer.valueType
        )
        val stringRedisSerializer = StringRedisSerializer()
        val context = RedisSerializationContext
            .newSerializationContext<String, Any>(StringRedisSerializer())
            .key(stringRedisSerializer)
            .value(valueSerializer)
            .hashKey(stringRedisSerializer)
            .hashValue(valueSerializer)
            .build()
        return ReactiveRedisTemplate(factory, context)
    }

    private fun valueRedisSerializer(
        resourceLoader: ResourceLoader,
        objectMapper: ObjectMapper,
        type: RedisSerializerType,
    ): RedisSerializer<Any> {
        return when (type) {
            RedisSerializerType.JSON -> jsonRedisSerializer(objectMapper)
            RedisSerializerType.JDK -> jdkRedisSerializer(resourceLoader)
        }
    }

    private fun jdkRedisSerializer(resourceLoader: ResourceLoader): JdkSerializationRedisSerializer {
        return JdkSerializationRedisSerializer(resourceLoader.classLoader)
    }

    private fun jsonRedisSerializer(objectMapper: ObjectMapper): GenericJackson2JsonRedisSerializer {
        val typer = TypeResolverBuilder(
            DefaultTyping.EVERYTHING,
            objectMapper.polymorphicTypeValidator
        )
            .init(JsonTypeInfo.Id.CLASS, null as TypeIdResolver?)
            .inclusion(JsonTypeInfo.As.PROPERTY)
        var om = objectMapper.copy()
        om = om.setDefaultTyping(typer)
        return GenericJackson2JsonRedisSerializer(om)
    }

    private class TypeResolverBuilder(t: DefaultTyping, ptv: PolymorphicTypeValidator) :
        DefaultTypeResolverBuilder(t, ptv) {
        override fun withDefaultImpl(defaultImpl: Class<*>?): DefaultTypeResolverBuilder {
            return this
        }

        override fun useForType(javaType: JavaType): Boolean {
            var t = javaType
            return if (t.isJavaLangObject) {
                true
            } else {
                t = resolveArrayOrWrapper(t)
                if (!t.isEnumType && !ClassUtils.isPrimitiveOrWrapper(t.rawClass)) {
                    if (t.isFinal && !KotlinDetector.isKotlinType(t.rawClass) && t.rawClass.packageName.startsWith(
                            "java"
                        )
                    ) {
                        false
                    } else {
                        !TreeNode::class.java.isAssignableFrom(t.rawClass)
                    }
                } else {
                    false
                }
            }
        }

        private fun resolveArrayOrWrapper(javaType: JavaType): JavaType {
            var type = javaType
            while (type.isArrayType) {
                type = type.contentType
                if (type.isReferenceType) {
                    type = resolveArrayOrWrapper(type)
                }
            }
            while (type.isReferenceType) {
                type = type.referencedType
                if (type.isArrayType) {
                    type = resolveArrayOrWrapper(type)
                }
            }
            return type
        }
    }
}
