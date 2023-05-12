package com.itheima.reggie.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig extends CachingConfigurerSupport {
@Bean
   public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory connectionFactory){
       //用于解决设置key查询时与设置不一致
       //因为序列化方式不同，redis默认key序列化器为：JdkSerializationRedisSerializer
       RedisTemplate<Object,Object> redisTemplate=new RedisTemplate<>();
       redisTemplate.setKeySerializer(new StringRedisSerializer());
       redisTemplate.setHashKeySerializer(new StringRedisSerializer());

       redisTemplate.setConnectionFactory(connectionFactory);
       return redisTemplate;
   }
}
