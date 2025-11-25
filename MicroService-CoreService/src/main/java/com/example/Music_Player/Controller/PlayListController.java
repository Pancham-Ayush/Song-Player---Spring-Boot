
package com.example.Music_Player.Controller;

import com.example.Music_Player.Model.Playlist;
import com.example.Music_Player.Model.Song;
import com.example.Music_Player.Repository.PlaylistRepo;
import com.example.Music_Player.Repository.SongRepo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.support.FeignUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayListController {

    private final PlaylistRepo playlistRepo;

    private final SongRepo songRepo;

    private final Executor virtualThreadExecutor;

    public PlayListController(PlaylistRepo playlistRepo, SongRepo songRepo,  Executor VirtualThreadExecutor) {
        this.playlistRepo = playlistRepo;
        this.songRepo = songRepo;
        this.virtualThreadExecutor = VirtualThreadExecutor;
    }

    @PostMapping({"/createplaylist"})
    public ResponseEntity<?> createPlaylist(@RequestBody Map<String, Object> request) throws ExecutionException, InterruptedException {
        Future<ResponseEntity<Map<String,String>>> future = ((ExecutorService) virtualThreadExecutor)
                .submit(
                    ()-> {
                        String playlistName = (String) request.get("name");
                        String email = (String) request.get("email");
                        Boolean publicPlaylistFlag = Boolean.FALSE;
                        if (request.get("public_playlist") != null)
                            publicPlaylistFlag = (Boolean) request.get("public_playlist");

                        if (email != null && !email.isBlank()) {
                            if (playlistName != null && !playlistName.isBlank()) {
                                Playlist playlist = new Playlist();
                                playlist.setName(playlistName);
                                playlist.setUserEmail(email);
                                playlist.setPublicplaylist(publicPlaylistFlag.toString());
                                this.playlistRepo.save(playlist);
                                return ResponseEntity.ok(Map.of("message", "Playlist created successfully"));

                            } else {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Playlist name cannot be empty"));
                            }
                        } else {
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not logged in or email is empty"));
                        }
                    });
        return future.get();
    }

    @SneakyThrows
    @PostMapping({"/getplaylist"})
    public ResponseEntity<Map<String, List<Playlist>>> getPlaylist(@RequestBody Map<String, String> request) {
        Future<ResponseEntity<Map<String,List<Playlist>>>> future = ((ExecutorService) virtualThreadExecutor)
                .submit(()-> {
                    String email = (String) request.get("email");
                    List<Playlist> playlists = this.playlistRepo.findPlaylistsByUserEmail(email);
                    return ResponseEntity.ok(Map.of("playlists", playlists));
                });
        return future.get();
    }
    @GetMapping({"/playlistsongs"})
    public ResponseEntity<Map<String,Object>> getPlaylistSongs(@RequestParam("playlistid") String playlistId) throws ExecutionException, InterruptedException {
        Future<ResponseEntity<Map<String,Object>>> future = ((ExecutorService) virtualThreadExecutor)
                .submit(()-> {
                    Optional<Playlist> playlistOpt = this.playlistRepo.findById(playlistId);
                    if (playlistOpt.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Playlist not found"));
                    } else {
                        List<Song> songs = ((Playlist) playlistOpt.get()).getSongs();
                        return ResponseEntity.ok(Map.of("songs", songs));
                    }
                });
        return future.get();
    }

    @GetMapping({"/publicplaylist"})
    public ResponseEntity<?> getPublicPlaylist() {
        return ResponseEntity.ok(Map.of("public_playlist", this.playlistRepo.findPlaylistsByPublicplaylist("true")));
    }

    @PostMapping("/addtoplaylist")
    public ResponseEntity<?> addToPlaylist(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String songId = request.get("songid");
        String playlistId = request.get("playlistid");

        if (email == null || songId == null || playlistId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email, song ID, and playlist ID are required"));
        }

        try {

            Optional<Playlist> playlistOpt = playlistRepo.findById(playlistId);
            if (playlistOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Playlist not found with ID: " + playlistId));
            }

            Playlist playlist = playlistOpt.get();
            if (!Objects.equals(playlist.getUserEmail(), email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Playlist does not belong to user"));
            }

            Song song = songRepo.findById(songId).get();
            playlist.getSongs().removeIf(s -> s.getId().equals(song.getId()));

            // Add song at the end
            playlist.getSongs().add(song);

            playlistRepo.save(playlist);

            return ResponseEntity.ok(Map.of("message", "Song added/moved to end of playlist successfully"));

        } catch (Exception e) {
            e.printStackTrace(); // For debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to add song to playlist"));
        }
    }

}
