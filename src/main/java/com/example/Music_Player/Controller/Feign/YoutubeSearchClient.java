package com.example.Music_Player.Controller.Feign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "youtubeClient", url = "${SEARCH_URL}")
public interface YoutubeSearchClient {
    @GetMapping("/search")
    String ytSearchCall(
            @RequestParam("part") String part,
            @RequestParam("type") String type,
            @RequestParam("maxResults") int maxResults,
            @RequestParam("q") String query,
            @RequestParam("safeSearch") String safeSearch,
            @RequestParam("order") String order,
            @RequestParam("key") String apiKey,
            @RequestParam(value = "pageToken", required = false) String pageToken
    );
}
