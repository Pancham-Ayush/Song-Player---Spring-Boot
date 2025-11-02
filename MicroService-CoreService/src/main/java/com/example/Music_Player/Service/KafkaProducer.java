package com.example.Music_Player.Service;

import com.example.Music_Player.Model.YoutubeVideo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.name}")
    String topic;

    public void publishDownloadRequest(Object youtubeVideo) {
        this.kafkaTemplate.send(topic, youtubeVideo);
    }
    public void publishManual(String topic, Object object) {
        this.kafkaTemplate.send(topic, object);

    }
}
