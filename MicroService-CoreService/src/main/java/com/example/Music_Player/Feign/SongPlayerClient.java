package com.example.Music_Player.Feign;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.core.io.Resource;

// NO NEED, BYPASSED WITH API GATEWAY + REDUCED LATENCY
@FeignClient(name = "S3-Service")
public interface SongPlayerClient {

    @GetMapping(value = "/get/{songid}", consumes = "application/octet-stream")
    ResponseEntity<Resource> getSong(
            @PathVariable("songid") String songid,
            @RequestHeader(value = "Range", required = false) String range
    );
}
// NO NEED, BYPASSED WITH API GATEWAY + REDUCED LATENCY

