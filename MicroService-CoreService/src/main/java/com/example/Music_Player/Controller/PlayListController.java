
package com.example.Music_Player.Controller;

import com.example.Music_Player.Model.Playlist;
import com.example.Music_Player.Model.Song;
import com.example.Music_Player.Model.User;
import com.example.Music_Player.Repository.PlaylistRepo;
import com.example.Music_Player.Repository.SongRepo;
import com.example.Music_Player.Repository.UserRepo;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayListController {
    @Autowired
    private PlaylistRepo playlistRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private SongRepo songRepo;

    @PostMapping({"/createplaylist"})
    public ResponseEntity<?> createPlaylist(@RequestBody Map<String, Object> request) {
        String playlistName = (String)request.get("name");
        String email = (String)request.get("email");
        Boolean publicPlaylistFlag = Boolean.FALSE;
        if (request.get("public_playlist") != null) {
            publicPlaylistFlag = (Boolean)request.get("public_playlist");
        }

        if (email != null && !email.isBlank()) {
            if (playlistName != null && !playlistName.isBlank()) {
                User user = this.userRepo.findByEmail(email);
                if (user == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "No user found with email: " + email));
                } else {
                    Playlist playlist = new Playlist();
                    playlist.setName(playlistName);
                    playlist.setUserEmail(user.getEmail());
                    playlist.setPublicplaylist(publicPlaylistFlag.toString());
                    this.playlistRepo.save(playlist);
                    return ResponseEntity.ok(Map.of("message", "Playlist created successfully"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Playlist name cannot be empty"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not logged in or email is empty"));
        }
    }

    @PostMapping({"/getplaylist"})
    public ResponseEntity<?> getPlaylist(@RequestBody Map<String, String> request) {
        String email = (String)request.get("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Email is required"));
        } else {
            User user = this.userRepo.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "No user found with email: " + email));
            } else {
                List<Playlist> playlists = this.playlistRepo.findPlaylistsByUserEmail(user.getEmail());
                return ResponseEntity.ok(Map.of("playlists", playlists));
            }
        }
    }

    @GetMapping({"/playlistsongs"})
    public ResponseEntity<?> getPlaylistSongs(@RequestParam("playlistid") String playlistId) {
        Optional<Playlist> playlistOpt = this.playlistRepo.findById(playlistId);
        if (playlistOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Playlist not found"));
        } else {
            List<Song> songs = ((Playlist)playlistOpt.get()).getSongs();
            return ResponseEntity.ok(Map.of("songs", songs));
        }
    }

    @GetMapping({"/publicplaylist"})
    public ResponseEntity<?> getPublicPlaylist() {
        return ResponseEntity.ok(Map.of("public_playlist", this.playlistRepo.findPlaylistsByPublicplaylist("true")));
    }
}
