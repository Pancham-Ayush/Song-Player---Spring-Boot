package com.example.SearchEngine_MicroService.Repo;

import com.example.SearchEngine_MicroService.GraphQl.GraphQL_DTO.SongPage;
import com.example.SearchEngine_MicroService.Model.Song;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Optional;

@Repository
public class SongRepo {

    private final DynamoDbEnhancedClient client;

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


    public SongPage findAll(String lastId, int chunkSize) {
        ScanEnhancedRequest.Builder builder = ScanEnhancedRequest.builder()
                .limit(chunkSize)
                .attributesToProject("id", "name", "artist");

        if (lastId != null) {
            builder.exclusiveStartKey(
                    Map.of("id", AttributeValue.builder().s(lastId).build())
            );
        }

        Page<Song> page = table.scan(builder.build())
                .iterator()
                .next();

        Map<String, AttributeValue> lek = page.lastEvaluatedKey();

        String nextCursor = null;
        if (lek != null && lek.get("id") != null && lek.get("id").s() != null) {
            nextCursor = lek.get("id").s();
        }


        return SongPage.builder()
                .content(page.items())
                .nextCursor(nextCursor)
                .chunkSize(chunkSize)
                .build();
    }

}