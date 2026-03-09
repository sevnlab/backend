package com.example.backend.study.redis.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * RedisTemplate 占쏙옙占쏙옙 占쏙옙占쏙옙占?占쏙옙占쏙옙 Redis 占쏙옙占쏙옙
 * RedisTemplate占쏙옙 占쏙옙占쏙옙 占쏙옙占쌉받억옙 Redis占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙 占쏙옙占?
 */
// @Configuration  // Redis ?꾩떆 鍮꾪솢?깊솕
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * 占쏙옙占시몌옙占쏙옙占싱쇽옙 占쏙옙占쏙옙占?占쏙옙占쏜스울옙 占쏙옙占쏙옙占?占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 占십깍옙화, 占쏘영환占쏙옙첼占쏙옙占?占쌍쇽옙처占쏙옙 (占쏙옙占쌩시울옙占쏙옙 占쏙옙占?
     */
    @PostConstruct
    public void clearRedisOnStartup() {
        redisConnectionFactory.getConnection()
            .serverCommands()
            .flushDb();
    }

    /**
     * ? 占쏙옙占쌘울옙 占쏙옙占쏙옙 占쏙옙占시몌옙
     * - opsForValue().set("key", "value")
     * - CLI占쏙옙 100% 호환占쏙옙
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    /**
     * ? JSON 占쏙옙占쏙옙占?RedisTemplate
     * - Key占쏙옙 占쏙옙占쌘울옙(StringRedisSerializer)
     * - Value占쏙옙 JSON 占쏙옙占쏙옙화(Jackson2JsonRedisSerializer)
     * - Hash 占쏙옙占쏙옙占쏙옙 JSON 占쏙옙占?
     * <p>
     * 占쏙옙 Redis-cli占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占?占쏙옙占쏙옙 占쏙옙 占쏙옙占쏙옙
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key, HashKey 占쏙옙 String
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value, HashValue 占쏙옙 Generic JSON Serializer
        // ? 占쌍쏙옙 Spring 占쏙옙占쏙옙 占쏙옙占?
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        return template;
    }
}

