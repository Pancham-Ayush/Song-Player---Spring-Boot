
package com.example.Music_Player.Controller;

import com.example.Music_Player.DTO.SONG_YT_DTO;
import com.example.Music_Player.Model.YoutubeVideo;
import com.example.Music_Player.AI.AIService;
import com.example.Music_Player.Kafka.KafkaProducer;
import com.example.Music_Player.Service.SearchYT;
import com.example.Music_Player.Service.SongService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AIYTSongController {
    private final SearchYT searchYT;

    private final AIService aiService;

    private final SongService songService;

    private final KafkaProducer kafkaProducer;

    private final ObjectMapper objectMapper;

    private final Executor virtualThreadExecutor;

    public AIYTSongController(SearchYT searchYT, AIService aiService, SongService songService, KafkaProducer kafkaProducer, ObjectMapper objectMapper,Executor virtualThreadExecutor) {
        this.searchYT = searchYT;
        this.aiService = aiService;
        this.songService = songService;
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }
    @GetMapping({"SearchOnYt"})
    public ResponseEntity<?> searchOnYt(@RequestParam String query, @RequestParam(required = false) String token) throws ExecutionException, InterruptedException {
            Future<ResponseEntity<Map<String,Object>>> future= ((ExecutorService)virtualThreadExecutor).submit(()-> {
                Map<String, Object> map = this.searchYT.search(query, token);
                return ResponseEntity.ok(map);
            });
            return future.get();
    }


    @PostMapping({"AiDownloading"})
    public ResponseEntity<Object> download(@RequestBody YoutubeVideo youtubeVideo) throws JsonProcessingException, ExecutionException, InterruptedException {
        Future<ResponseEntity<Object>> future = ((ExecutorService) virtualThreadExecutor).submit(()-> {
            System.out.println("-------------"+Thread.currentThread()+ "----------------");
            String ytDetail = youtubeVideo.toString();
            boolean check = this.aiService.AISongVerification(ytDetail);
            if (check) {
                SONG_YT_DTO dto_yt = aiService.AiSongMapping(ytDetail);
                log.info(dto_yt.toString());
                String dto_json = objectMapper.writeValueAsString(dto_yt);
                kafkaProducer.publishDownloadRequest(dto_json);
                return ResponseEntity.ok("Under AI review for verification. Please check back in the Songs section within the next few min.");
            } else {
                return ResponseEntity.ok("Your song didn’t pass AI verification. Please check and upload again. ");
            }
        });
        return future.get();
    }
}
