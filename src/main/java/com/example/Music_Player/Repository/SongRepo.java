package com.example.Music_Player.Repository;

import com.example.Music_Player.Model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
    public interface SongRepo extends JpaRepository<Song, Long> {

        @Query("SELECT s FROM Song s WHERE " +
                "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                "LOWER(s.artist) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        List<Song> searchSongs(@Param("keyword") String keyword);
    }


