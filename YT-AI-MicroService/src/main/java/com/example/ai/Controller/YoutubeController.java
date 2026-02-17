package com.example.ai.Controller;

import com.example.ai.AI.AIService;
import com.example.ai.DTO.SONG_YT_DTO;
import com.example.ai.Kafka.KafkaDownloadReq;
import com.example.ai.Model.YoutubeVideo;
import com.example.ai.Service.SearchYTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
@RestController
public class YoutubeController {
    private final SearchYTService searchYTService;

    private final AIService aiService;

    private final KafkaDownloadReq kafkaDownloadReq;

    private final ObjectMapper objectMapper;

    private final Executor virtualThreadExecutor;

    public YoutubeController(SearchYTService searchYTService, AIService aiService, KafkaDownloadReq kafkaDownloadReq, ObjectMapper objectMapper, @Qualifier("Virtual") Executor virtualThreadExecutor) {
        this.searchYTService = searchYTService;
        this.aiService = aiService;
        this.kafkaDownloadReq = kafkaDownloadReq;
        this.objectMapper = objectMapper;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    @GetMapping({"SearchOnYt"})
    public ResponseEntity<?> searchOnYt(@RequestParam String query, @RequestParam(required = false) String token) throws ExecutionException, InterruptedException {
        Future<ResponseEntity<Map<String, Object>>> future = ((ExecutorService) virtualThreadExecutor).submit(() -> {
            Map<String, Object> map = this.searchYTService.search(query, token);
            return ResponseEntity.ok(map);
        });
        return future.get();
    }

    @PostMapping({"AiDownloading"})
    public ResponseEntity<Object> download(@RequestBody YoutubeVideo youtubeVideo, HttpServletRequest request) throws JsonProcessingException, ExecutionException, InterruptedException {
        Future<ResponseEntity<Object>> future = ((ExecutorService) virtualThreadExecutor).submit(() -> {
            String ytDetail = youtubeVideo.toString();
            String userEmail = request.getHeader("X-User-Email");
            boolean check = this.aiService.aiSongVerification(ytDetail);
            if (check) {
                SONG_YT_DTO dto_yt = aiService.aiSongMapping(ytDetail, userEmail);
                String dto_json = objectMapper.writeValueAsString(dto_yt);
                kafkaDownloadReq.publishDownloadRequest(dto_json);
                return ResponseEntity.ok("Under ai review for verification. Please check back in the Songs section within the next few min.");
            } else {
                return ResponseEntity.ok("Your song didn’t pass ai verification. Please check and upload again. ");
            }
        });
        return future.get();
    }
}
