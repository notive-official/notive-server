package com.example.notiveserver.config

import com.example.notiveserver.application.oembed.dto.OEmbedInfoDto
import io.lettuce.core.resource.ClientResources
import io.lettuce.core.resource.DefaultClientResources
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(private val props: RedisProperties) {

    /** Lettuce 내부 타이머·이벤트루프 종료를 위한 리소스 Bean */
    @Bean(destroyMethod = "shutdown")
    fun clientResources(): ClientResources =
        DefaultClientResources.create()

    /** 단일 노드 RedisConnectionFactory */
    @Bean(destroyMethod = "destroy")
    @Primary
    fun lettuceConnectionFactory(
        clientResources: ClientResources
    ): LettuceConnectionFactory {
        // standalone(단일노드) 설정
        val standaloneConfig = RedisStandaloneConfiguration().apply {
            hostName = props.host
            port = props.port
            props.password?.let { password = RedisPassword.of(it) }
        }

        // Lettuce 클라이언트 설정
        val clientConfig = LettuceClientConfiguration.builder()
            .clientResources(clientResources)
            .build()

        return LettuceConnectionFactory(standaloneConfig, clientConfig)
    }

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory =
        LettuceConnectionFactory()

    @Bean
    fun redisTemplate(cf: RedisConnectionFactory): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            connectionFactory = cf
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = GenericJackson2JsonRedisSerializer()
            afterPropertiesSet()
        }
    }

    @Bean
    fun reactiveOEmbedRedisOperations(
        factory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisOperations<String, OEmbedInfoDto> {
        val serializer = Jackson2JsonRedisSerializer(OEmbedInfoDto::class.java)
        val context = RedisSerializationContext
            .newSerializationContext<String, OEmbedInfoDto>(StringRedisSerializer())
            .value(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
            .build()
        return ReactiveRedisTemplate(factory, context)
    }
}