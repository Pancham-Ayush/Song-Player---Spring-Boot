package com.example.YT_S3_MicroService.Kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Value("${spring.kafka.topic.name.open-search}")
    private String openSearchTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String openSearchSong) {
        kafkaTemplate.send(openSearchTopic, openSearchSong);
    }
}
