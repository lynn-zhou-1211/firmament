package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("开始创建redis模板对象...");
        RedisTemplate redisTemplate = new RedisTemplate();
        // 设置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 1. 设置 Key 的序列化器 (使用 String，这样 key 就不乱码了)
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 2. 设置 Value 的序列化器 (通常也用 String 或 JSON)
        // 如果你存的是对象，想看见 JSON，可以用 Jackson2JsonRedisSerializer，或者干脆统一都用 String
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 设置 Hash 的 Key 和 Value 序列化器
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }
}