package com.example.YT_S3_MicroService.ObjMapper;

import com.example.YT_S3_MicroService.DTO.SONG_YT_DTO;
import com.example.YT_S3_MicroService.Model.Song;
import org.springframework.stereotype.Component;

@Component
public class YT_Dto_to_Song {
    public Song SongMapper(SONG_YT_DTO dto) {
        Song song = new Song();
        song.setName(dto.getName());
        song.setGenre(dto.getGenre());
        song.setArtist(dto.getArtist());
        song.setDescription(dto.getDescription());
        return song;
    }
}
