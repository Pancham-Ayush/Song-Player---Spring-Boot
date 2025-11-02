package com.example.Music_Player.DTO;

import com.example.Music_Player.Model.Song;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class SONG_YT_DTO {

    String name;
    String artist;
    String genre;
    String description;
    String url;
}
