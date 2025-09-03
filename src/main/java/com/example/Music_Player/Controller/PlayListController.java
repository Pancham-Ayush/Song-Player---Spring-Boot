package com.example.Music_Player.Controller;

import com.example.Music_Player.Model.Playlist;
import com.example.Music_Player.Model.Song;
import com.example.Music_Player.Model.User;
import com.example.Music_Player.Repository.PlaylistRepo;
import com.example.Music_Player.Repository.SongRepo;
import com.example.Music_Player.Repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
public class PlayListController {

    @Autowired
    private PlaylistRepo playlistRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SongRepo songRepo;

    @PostMapping("/createplaylist")
    public ResponseEntity<?> createPlaylist(@RequestBody Map<String, String> request) {
        String playlistName = request.get("name");
        String email = request.get("email");

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not logged in (no email provided)"));
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "No user found with email: " + email));
        }

        Playlist playlist = new Playlist();
        playlist.setName(playlistName);
        playlist.setUserId(user.getId());
        String publicPlaylistStr = request.get("public_playlist");
        boolean isPublic = "true".equalsIgnoreCase(publicPlaylistStr);
        System.out.println(publicPlaylistStr);
        playlist.setPublicplaylist(isPublic);
        playlistRepo.save(playlist);

        return ResponseEntity.ok(Map.of("message", "Playlist created successfully"));
    }

    @Transactional
    @PostMapping("/addtoplaylist")
    public ResponseEntity<?> addToPlaylist(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String songIdStr = request.get("songid");
        String playlistIdStr = request.get("playlistid");

        if (email == null || songIdStr == null || playlistIdStr == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email, song ID, and playlist ID are required"));
        }

        try {
            Long songId = Long.parseLong(songIdStr);
            Long playlistId = Long.parseLong(playlistIdStr);

            User user = userRepo.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "No user found with email: " + email));
            }

            Optional<Playlist> playlistOpt = playlistRepo.findById(playlistId);
            if (playlistOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Playlist not found with ID: " + playlistId));
            }

            Playlist playlist = playlistOpt.get();
            if (!Objects.equals(playlist.getUserId(), user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Playlist does not belong to user"));
            }

            Optional<Song> songOpt = songRepo.findById(songId);
            if (songOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Song not found with ID: " + songId));
            }

            Song song = songOpt.get();

            if (playlist.getSongs().contains(song)) {
                return ResponseEntity.ok(Map.of("message", "Song is already in the playlist"));
            }

            playlist.getSongs().add(song);
            playlistRepo.save(playlist);

            return ResponseEntity.ok(Map.of("message", "Song added to playlist successfully"));

        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid song ID or playlist ID format"));
        } catch (Exception e) {
            e.printStackTrace(); // For debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to add song to playlist"));
        }
    }

    @PostMapping("/getplaylist")
    public ResponseEntity<?> getPlaylist(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email is required"));
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "No user found with email: " + email));
        }

        List<Playlist> playlists = playlistRepo.findPlaylistsByUserId(user.getId());
        return ResponseEntity.ok(Map.of("playlists", playlists));
    }

    @GetMapping("/playlistsongs")
    public ResponseEntity<?> getPlaylistSongs(@RequestParam("playlistid") Long playlistId) {
        Optional<Playlist> playlistOpt = playlistRepo.findById(playlistId);

        if (playlistOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Playlist not found"));
        }

        List<Song> songs = playlistOpt.get().getSongs();
        return ResponseEntity.ok(Map.of("songs", songs));
    }

    @GetMapping("/publicplaylist")
    public ResponseEntity<?> getPublicPlaylist() {
        return ResponseEntity.ok(Map.of("public_playlist", playlistRepo.findPlaylistsBypublicplaylist(true)));
    }
}
