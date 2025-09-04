package com.example.Music_Player.Service;

import com.example.Music_Player.Model.Playlist;
import com.example.Music_Player.Model.Song;
import com.example.Music_Player.Repository.PlaylistRepo;
import com.example.Music_Player.Repository.SongRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeleteService {
    @Autowired
    SongRepo songRepo;
    @Autowired
    PlaylistRepo playlistRepo;

    public void deleteSong(String songId) {
        Optional<Song> songOptional = songRepo.findById(songId);
        if (songOptional.isEmpty()) {
            throw new RuntimeException("Song not found");
        }
        Song songToDelete = songOptional.get();



        // Delete the song safely
        songRepo.deleteSong(songId);
    }
}
