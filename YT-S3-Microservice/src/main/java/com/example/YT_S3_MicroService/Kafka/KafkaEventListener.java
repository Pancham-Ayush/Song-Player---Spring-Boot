package com.example.YT_S3_MicroService.Kafka;

import com.example.YT_S3_MicroService.DTO.SONG_YT_DTO;
import com.example.YT_S3_MicroService.Model.Song;
import com.example.YT_S3_MicroService.ObjMapper.YT_Dto_to_Song;
import com.example.YT_S3_MicroService.Service.YT_DLP_Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@Configuration
public class KafkaEventListener {

    private final YT_DLP_Service yt_dlp_service;

    private final YT_Dto_to_Song yt_dto_to_song;

    private final ObjectMapper objectMapper;

    public KafkaEventListener(YT_DLP_Service yt_dlp_service, YT_Dto_to_Song yt_dto_to_song, ObjectMapper objectMapper) {
        this.yt_dlp_service = yt_dlp_service;
        this.yt_dto_to_song = yt_dto_to_song;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "my-group-id")
    public void handleMessage(String jsonData) {
        log.info("Received message: {}", jsonData);
        SONG_YT_DTO yt_dto;
        try {
            yt_dto = objectMapper.readValue(jsonData, SONG_YT_DTO.class);
        } catch (JsonProcessingException e) {
            return;
        }
        try {
            Song song = yt_dto_to_song.SongMapper(yt_dto);
            String url = yt_dto.getUrl();
            System.out.println("+++" + yt_dto.toString());
            yt_dlp_service.uploadYoutubeAudioAsync(url, song, yt_dto.getEmail());
        } catch (Exception e) {
            log.error("Error while handling Kafka message: {}", e.getMessage(), e);
        }
    }
}
