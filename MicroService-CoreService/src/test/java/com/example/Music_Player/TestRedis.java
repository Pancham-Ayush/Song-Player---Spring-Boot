package com.example.Music_Player;

import com.example.Music_Player.Model.User;
import com.example.Music_Player.Redis.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestRedis {
@Autowired
public RedisService redisService;
     @Test
    void t1(){
         System.out.println(redisService.get("1").toString());
    }

    @Test
    void t2(){
         User user = new User();
         user.setEmail("sdfghjk");
         redisService.set("2",user);
    }
    @Test
    void t3(){
        System.out.println(redisService.get("2").toString());
    }

}