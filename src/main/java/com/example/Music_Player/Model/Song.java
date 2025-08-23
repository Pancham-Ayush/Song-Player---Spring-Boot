package com.example.Music_Player.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    Long id;
    String name;
    String artist;
    String genre;
    String description;
    String path;
    // Removed bidirectional mapping to avoid database schema issues
     @ManyToMany(mappedBy = "songs")
     @JsonIgnore
     private List<Playlist> playlists;


}
