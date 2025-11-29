package com.example.SearchEngine_MicroService.Kafka;

import com.example.SearchEngine_MicroService.Service.ElasticSearchService;
import com.example.SearchEngine_MicroService.Model.Song;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaOpenSearchListener {

    private final ObjectMapper objectMapper;

    private final ElasticSearchService elasticSearchService;

    public KafkaOpenSearchListener( ObjectMapper objectMapper,  ElasticSearchService elasticSearchService ) {
        this.objectMapper = objectMapper;
        this.elasticSearchService = elasticSearchService;
    }

    @KafkaListener(topics = "${spring.kafka.topic.name.open-search}", groupId = "my-group-id")
    public void listen(String songJson) {
        try {
            System.out.println(songJson);
            // Convert JSON to Song object
            Song song = objectMapper.readValue(songJson, Song.class);
            elasticSearchService.uploadSong(song);

        }
        catch (Exception e) {}
    }
}
