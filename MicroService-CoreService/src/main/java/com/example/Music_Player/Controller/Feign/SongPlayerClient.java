package com.example.Music_Player.Controller.Feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "SongPlayer", url = "http://localhost:8081")
public interface SongPlayerClient {
    @GetMapping(value = "/get/{songid}", consumes = "application/octet-stream")
    ResponseEntity<Resource> getSong(
            @PathVariable("songid") String songid,
            @RequestHeader(value = "Range", required = false) String range
    );
}


