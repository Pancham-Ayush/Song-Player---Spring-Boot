package com.example.Music_Player.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate redisTemplate;

    public <T>  T get(String key){
        return (T) redisTemplate.opsForValue().get(key)  ;
    }
    public void  set(String key, Object value){
        redisTemplate.opsForValue().set(key,value,300, TimeUnit.SECONDS) ;
    }



}
