package com.example.Music_Player.Repository;

import com.example.Music_Player.Model.Playlist;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PlaylistRepo {
    @Autowired
    private DynamoDbEnhancedClient client;
    private DynamoDbTable<Playlist> playlistTable;

    @PostConstruct
    void init() {
        this.playlistTable=client.table("Playlist", TableSchema.fromBean(Playlist.class));
    }

    public Playlist save(Playlist playlist) {
        playlistTable.putItem(playlist);
        return playlist;
    }

    public Optional<Playlist> findById(String id) {
        return Optional.ofNullable(
                playlistTable.getItem(r -> r.key(k -> k.partitionValue(id)))
        );
    }

    public List<Playlist> findPlaylistsByUserEmail(String email) {
        return playlistTable.index("UserEmailIndex")
                .query(r -> r.queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(email))))
                .stream()
                .flatMap(page -> page.items().stream())
                .toList();
    }

    public List<Playlist> findPlaylistsByPublicplaylist(String publicplaylist) {
        return playlistTable.index("PublicPlaylistIndex")
                .query(q -> q.queryConditional(
                        QueryConditional.keyEqualTo(k -> k.partitionValue(publicplaylist))
                ))
                .stream()
                .flatMap(page -> page.items().stream())
                .toList();
    }

    public List<Playlist> findAll() {
        return playlistTable.scan().items().stream().collect(Collectors.toList());
    }

    // Existing methods, renamed for clarity
    public List<Playlist> getAllPlaylistsByEmail(String email) {
        return findPlaylistsByUserEmail(email);
    }

    public List<Playlist> getPublicPlaylists() {
        return findPlaylistsByPublicplaylist("true");
    }
}