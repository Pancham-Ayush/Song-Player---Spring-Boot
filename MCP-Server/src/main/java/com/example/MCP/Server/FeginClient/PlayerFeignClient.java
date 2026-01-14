package com.example.MCP.Server.FeginClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "playerClient",
        url = "http://localhost:37129"   // 👈 DIRECT, NO DISCOVERY
)
public interface PlayerFeignClient {

    @GetMapping(value = "/get/{songid}", produces = "audio/opus")
    ResponseEntity<Resource> getSong(
            @PathVariable("songid") String songid,
            @RequestHeader(name = "Range", required = false) String range
    );

}
