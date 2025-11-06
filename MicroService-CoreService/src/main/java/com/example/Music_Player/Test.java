package com.example.Music_Player;

import com.example.Music_Player.Kafka.KafkaProducer;
import com.example.Music_Player.Model.Song;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.HttpResource;

@RestController
public class Test {
    @Autowired
    KafkaProducer kafkaProducer;
    @GetMapping("t1")
    public ResponseEntity<?> t1() throws JsonProcessingException {
        Song song = new Song();
        song.setSize(7654l);
        song.setName("test");
        song.setArtist("test32");
        song.setDescription("4321");
        ObjectMapper mapper = new ObjectMapper();
        String json =mapper.writeValueAsString(song);
        kafkaProducer.publishDownloadRequest(json);
        return ResponseEntity.ok(json);
    }
}
