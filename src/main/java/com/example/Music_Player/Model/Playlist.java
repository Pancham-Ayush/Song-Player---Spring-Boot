package com.example.Music_Player.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@DynamoDbBean
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {

    private String id= UUID.randomUUID().toString();
    private String name;
    private String userEmail;
    private List<Song> songs = new ArrayList<>();
    private String publicplaylist;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
    @DynamoDbSecondaryPartitionKey(indexNames = "UserEmailIndex")
    public String getUserEmail() {
        return userEmail;
    }
    @DynamoDbSecondaryPartitionKey(indexNames = "PublicPlaylistIndex")
    public String getPublicplaylist() {
        return publicplaylist;
    }
}
