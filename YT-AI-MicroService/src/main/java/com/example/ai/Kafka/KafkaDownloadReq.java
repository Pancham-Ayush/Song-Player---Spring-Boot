package com.example.ai.Kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaDownloadReq {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.name}")
    String topic;

    public KafkaDownloadReq(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishDownloadRequest(String youtubeVideo)
    {
        this.kafkaTemplate.send(topic, youtubeVideo);
    }

}
