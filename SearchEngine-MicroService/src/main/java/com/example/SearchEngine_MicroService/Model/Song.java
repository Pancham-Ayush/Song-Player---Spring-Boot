package com.example.SearchEngine_MicroService.Model;


import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
@DynamoDbBean
public class Song {

    String id ;
    String name;
    String artist;
    String genre;
    String description;
    String path;
    Long size;

    @DynamoDbPartitionKey
    public String getId(){
        return id;
    }

}
