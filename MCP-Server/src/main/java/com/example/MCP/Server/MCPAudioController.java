package com.example.MCP.Server;

import com.example.MCP.Server.FeginClient.PlayerFeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mcp/audio")
public class MCPAudioController {

    private final PlayerFeignClient playerFeignClient;

    public MCPAudioController(PlayerFeignClient playerFeignClient) {
        this.playerFeignClient = playerFeignClient;
    }

    @GetMapping("/play/{songid}")
    public ResponseEntity<Resource> play(
            @PathVariable String songid,
            @RequestHeader(value = "Range", required = false) String range
    ) {
        ResponseEntity<Resource> response =
                playerFeignClient.getSong(songid, range);

        HttpHeaders headers = new HttpHeaders();
        headers.putAll(response.getHeaders());

        return ResponseEntity
                .status(response.getStatusCode())
                .headers(headers)
                .body(response.getBody());
    }

}
