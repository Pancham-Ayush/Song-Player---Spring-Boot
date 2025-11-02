package com.example.YT_S3_MicroService.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.UUID;


@DynamoDbBean
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Song {

    String id = UUID.randomUUID().toString();
    String name;
    String artist;
    String genre;
    String description;
    String path;
    Long size;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }


}
