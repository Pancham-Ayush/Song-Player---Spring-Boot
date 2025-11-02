package com.example.YT_S3_MicroService.DTO;

import com.example.Music_Player.Model.Song;

import java.util.UUID;

public class SONG_YT_DTO {
    String id;
    String name;
    String artist;
    String genre;
    String description;
    String path;
    Long size;
    String url;
    
    SONG_YT_DTO(Song song, String url){
        this.id = song.getId();
        this.name = song.getName();
        this.artist = song.getArtist();
        this.genre = song.getGenre();
        this.description = song.getDescription();
        this.path = song.getPath();
        this.size = song.getSize();
        this.url = url;
    }
}
