package com.example.Music_Player.Kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.name}")
    String topic;

    public void publishDownloadRequest(String youtubeVideo)
    {
        this.kafkaTemplate.send(topic, youtubeVideo);
    }
    public void publishManual(String topic, String object) {
        this.kafkaTemplate.send(topic, object);

    }
}
