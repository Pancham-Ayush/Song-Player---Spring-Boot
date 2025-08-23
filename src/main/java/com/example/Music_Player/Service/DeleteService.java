package com.example.Music_Player.Service;

import com.example.Music_Player.Model.Playlist;
import com.example.Music_Player.Model.Song;
import com.example.Music_Player.Repository.PlaylistRepo;
import com.example.Music_Player.Repository.SongRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteService {
    @Autowired
    SongRepo songRepo;
    @Autowired
    PlaylistRepo playlistRepo;

    @Transactional
    public void deleteSong(Long songId) {
        Song song = songRepo.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        // Remove song from all playlists
        for (Playlist playlist : song.getPlaylists()) {
            playlist.getSongs().remove(song);
            playlistRepo.save(playlist); // persist the removal
        }

        // Clear playlists in song entity to avoid Hibernate issues
        song.getPlaylists().clear();
        songRepo.save(song);

        // Delete the song safely
        songRepo.delete(song);
    }



}
