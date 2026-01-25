package com.example.YT_S3_MicroService.Repository;

import com.example.YT_S3_MicroService.Model.Song;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class SongRepo {

    private final DynamoDbEnhancedClient client;

    DynamoDbTable<Song> table;

    public SongRepo(DynamoDbEnhancedClient client) {
        this.client = client;
    }

    @PostConstruct
    void init() {
        table = client.table("Song", TableSchema.fromBean(Song.class));
    }

    public Song saveSong(Song song) {
        table.putItem(song);
        return song;
    }

}