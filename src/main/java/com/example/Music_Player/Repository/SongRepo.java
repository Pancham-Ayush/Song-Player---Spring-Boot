package com.example.Music_Player.Repository;

import com.example.Music_Player.Model.Song;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SongRepo {

    @Autowired
    DynamoDbEnhancedClient client;

    DynamoDbTable<Song> table;

    @PostConstruct
    void init() {
        table=client.table("Song", TableSchema.fromBean(Song.class));

    }

    public Optional<Song> findById(String id) {
        return Optional.ofNullable(table.getItem(r -> r.key(Key.builder().partitionValue(id).build())));
    }

    public Song deleteSong(String id) {
        return table.deleteItem(r -> r.key(Key.builder().partitionValue(id).build()));
    }

    public Song saveSong(Song song) {
        table.putItem(song);
        return song;
    }

    public List<Song> findAll() {
        return table.scan().items().stream().collect(Collectors.toList());
    }

    public List<Song> findAll(int page, int chunkSize) {
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .limit(chunkSize)
                .build();

        List<Page<Song>> pages = table.scan(request).stream().collect(Collectors.toList());

        if (page >= pages.size()) {
            return List.of(); // Return empty list if page is out of bounds
        }

        return pages.get(page).items();
    }

}