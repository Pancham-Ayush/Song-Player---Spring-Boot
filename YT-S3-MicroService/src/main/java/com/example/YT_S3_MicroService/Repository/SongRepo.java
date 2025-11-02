package com.example.YT_S3_MicroService.Repository;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SongRepo {

    final DynamoDbEnhancedClient client;

    DynamoDbTable<Song> table;

    public SongRepo(DynamoDbEnhancedClient client) {
        this.client = client;
    }

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

    public Map<String,Object> findAll(int page, int chunkSize) {



        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .limit(chunkSize)
                .attributesToProject("id","name","artist")
                .build();

        List<Page<Song>> pages = table.scan(request).stream().collect(Collectors.toList());
        int totalPages = pages.size();

        List<Song> songs = (page < totalPages) ? pages.get(page).items() : List.of();

        Map<String, Object> result = new HashMap<>();
        result.put("content", songs);
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("chunkSize", chunkSize);

        return result;
    }



}