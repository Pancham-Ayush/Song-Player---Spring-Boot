
package com.example.Music_Player.Service;

import com.example.Music_Player.Controller.Feign.YoutubeSearchClient;
import com.example.Music_Player.Model.YoutubeVideo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SearchYT {
    @Value("${SEARCH_URL}")
    private String SEARCH_URL;
    @Value("${youtube.api.key}")
    private String apiKey;
    @Autowired
    private AiService aiService;
    @Autowired
    private YoutubeSearchClient  youtubeSearchClient;
    public Map<String, Object> search(String search, String pageToken) {
        String url = this.SEARCH_URL;
        if (pageToken == null || pageToken.isEmpty()) {
            pageToken=null;
        }
        String response = youtubeSearchClient.ytSearchCall(
                "snippet",
                "video",
                10,
                search,
                "moderate",
                "viewCount",
                apiKey,
                pageToken
        );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;

        try {
            root = mapper.readTree(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<YoutubeVideo> yt = new ArrayList();

        for(JsonNode item : root.path("items")) {
            String title = item.path("snippet").path("title").asText();
            String videoId = item.path("id").path("videoId").asText();

            String ytUrl = "https://www.youtube.com/watch?v=" + videoId;
            String thumbnail = item.path("snippet").path("thumbnails").path("high").path("url").asText();
            String channel = item.path("snippet").path("channelTitle").asText();
            String description = item.path("snippet").path("description").asText();
            YoutubeVideo video = new YoutubeVideo(title, ytUrl, thumbnail, channel, description);
            yt.add(video);
        }

        String nextPageId = root.path("nextPageToken").asText();
        String prevPageId = root.path("prevPageToken").asText();
        Map<String, Object> map = new HashMap();
        map.put("nextPageToken", nextPageId);
        map.put("prevPageToken", prevPageId);
        map.put("yt", yt);
        return map;
    }
}
