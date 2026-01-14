package com.example.MCP.Server;

import com.example.MCP.Server.FeginClient.PlayerFeignClient;
import com.example.MCP.Server.FeginClient.SearchFeignClient;
import org.springframework.ai.tool.annotation.Tool;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class SongTool {

    private final SearchFeignClient searchFeignClient;
    private final PlayerFeignClient playerFeignClient;

    public SongTool(SearchFeignClient searchFeignClient,
                    PlayerFeignClient playerFeignClient) {
        this.searchFeignClient = searchFeignClient;
        this.playerFeignClient = playerFeignClient;
    }

    @Tool(
            name = "song_list",
            description = "Fetch paginated list of songs from the music microservice"
    )
    public Map<String, Object> getAllSongs(int page, int chunk) {
        return searchFeignClient.getAllSongs(page, chunk).getBody();
    }


    @Tool(
            name = "song_player",
            description = "Play a song using its ID"
    )
    public Map<String, String> playSong(String songid) {

        String playUrl = "http://localhost:7412/mcp/audio/play/" + songid;

        return Map.of(
                "action", "PLAY",
                "songId", songid,
                "streamUrl", playUrl,
                "message", "Playing song now"
        );
    }



}
