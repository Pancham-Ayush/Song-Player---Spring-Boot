package com.example.SearchEngine_MicroService.Repo;

import com.example.SearchEngine_MicroService.Model.Playlist;
import com.example.SearchEngine_MicroService.Model.Song;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PlaylistRepo {

    private final DynamoDbEnhancedClient client;
    private DynamoDbTable<Playlist> playlistTable;

    public PlaylistRepo(DynamoDbEnhancedClient client) {
        this.client = client;
    }

    @PostConstruct
    void init() {
        this.playlistTable = this.client.table("Playlist", TableSchema.fromBean(Playlist.class));
    }

    public Playlist save(Playlist playlist) {
        this.playlistTable.putItem(playlist);
        return playlist;
    }

    public Optional<Playlist> findById(String id) {
        return Optional.ofNullable((Playlist) this.playlistTable.getItem((r) -> r.key((k) -> k.partitionValue(id))));
    }

    public List<Playlist> findPlaylistsByUserEmail(String email) {
        return this.playlistTable.index("UserEmailIndex").query((r) -> r.queryConditional(QueryConditional.keyEqualTo((k) -> k.partitionValue(email)))).stream().flatMap((page) -> page.items().stream()).toList();
    }

    public List<Playlist> findPlaylistsByPublicplaylist(String publicplaylist) {
        return this.playlistTable.index("PublicPlaylistIndex").query((q) -> q.queryConditional(QueryConditional.keyEqualTo((k) -> k.partitionValue(publicplaylist)))).stream().flatMap((page) -> page.items().stream()).toList();
    }

    public void deleteFromPlaylist(String id) {
        List<Playlist> playlists = playlistTable.scan()
                .items()
                .stream()
                .collect(Collectors.toList());

        List<Playlist> modifiedPlaylists = playlists.stream()
                .filter(playlist -> {
                    List<Song> songs = playlist.getSongs();
                    if (songs == null) return false;
                    return songs.removeIf(song -> song.getId().equals(id));
                })
                .collect(Collectors.toList());

        modifiedPlaylists.forEach(playlistTable::updateItem);
    }
}
