package com.example.Music_Player.Redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final RedisTemplate redisTemplate;

    public RedisService(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public <T>  T get(String key){
        return (T) redisTemplate.opsForValue().get(key)  ;
    }
    public void  set(String key, Object value){
        redisTemplate.opsForValue().set(key,value,300, TimeUnit.SECONDS) ;
    }



}
