package com.example.YT_S3_MicroService.Kafka;

import com.example.YT_S3_MicroService.DTO.SONG_YT_DTO;
import com.example.YT_S3_MicroService.Model.Song;
import com.example.YT_S3_MicroService.ObjMapper.YT_Dto_to_Song;
import com.example.YT_S3_MicroService.Service.yt_dlp_Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Map;

@Slf4j
@Configuration
public class KafkaEventListener {

    @Autowired
    private yt_dlp_Service yt_dlp_service;

    private static YT_Dto_to_Song yt_dto_to_song = new YT_Dto_to_Song();

    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "my-group-id")
    public void handleMessage(String jsonData) {
        log.info("Received message: {}", jsonData);
        SONG_YT_DTO yt_dto = null;
        try {
            yt_dto = objectMapper.readValue(jsonData, SONG_YT_DTO.class);
        } catch (JsonProcessingException e) {
            return;
        }
        try {
            Song song = yt_dto_to_song.SongMapper(yt_dto);
            String url = yt_dto.getUrl();
            yt_dlp_service.uploadYoutubeAudioAsync(url, song);
        } catch (Exception e) {
            log.error("Error while handling Kafka message: {}", e.getMessage(), e);
        }
    }
}
