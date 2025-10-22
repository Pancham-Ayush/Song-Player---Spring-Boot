
package com.example.Music_Player.Repository;

import com.example.Music_Player.Model.Playlist;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class PlaylistRepo {
    @Autowired
    private DynamoDbEnhancedClient client;
    private DynamoDbTable<Playlist> playlistTable;

    @PostConstruct
    void init() {
        this.playlistTable = this.client.table("Playlist", TableSchema.fromBean(Playlist.class));
    }

    public Playlist save(Playlist playlist) {
        this.playlistTable.putItem(playlist);
        return playlist;
    }

    public Optional<Playlist> findById(String id) {
        return Optional.ofNullable((Playlist)this.playlistTable.getItem((r) -> r.key((k) -> k.partitionValue(id))));
    }

    public List<Playlist> findPlaylistsByUserEmail(String email) {
        return this.playlistTable.index("UserEmailIndex").query((r) -> r.queryConditional(QueryConditional.keyEqualTo((k) -> k.partitionValue(email)))).stream().flatMap((page) -> page.items().stream()).toList();
    }

    public List<Playlist> findPlaylistsByPublicplaylist(String publicplaylist) {
        return this.playlistTable.index("PublicPlaylistIndex").query((q) -> q.queryConditional(QueryConditional.keyEqualTo((k) -> k.partitionValue(publicplaylist)))).stream().flatMap((page) -> page.items().stream()).toList();
    }

    public void delete(String id) {
        List<Playlist> playlists = (List)this.playlistTable.scan().items().stream().collect(Collectors.toList());
        List<Playlist> modifiedPlaylists = (List)playlists.stream().filter((playlist) -> playlist.getSongs().removeIf((song) -> song.getId().equals(id))).collect(Collectors.toList());
        DynamoDbTable var10001 = this.playlistTable;
        Objects.requireNonNull(var10001);
        modifiedPlaylists.forEach(var10001::updateItem);
    }

    public List<Playlist> findAll() {
        return (List)this.playlistTable.scan().items().stream().collect(Collectors.toList());
    }

    public List<Playlist> getAllPlaylistsByEmail(String email) {
        return this.findPlaylistsByUserEmail(email);
    }

    public List<Playlist> getPublicPlaylists() {
        return this.findPlaylistsByPublicplaylist("true");
    }
}
