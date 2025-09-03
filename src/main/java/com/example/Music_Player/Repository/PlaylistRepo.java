package com.example.Music_Player.Repository;

import com.example.Music_Player.Model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepo extends JpaRepository<Playlist,Long> {
    List<Playlist> findPlaylistsByUserId(Long userId);
    List<Playlist> findPlaylistsBypublicplaylist(boolean publicPlaylist);
}
