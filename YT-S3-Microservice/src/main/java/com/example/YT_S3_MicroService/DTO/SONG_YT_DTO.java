package com.example.YT_S3_MicroService.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.awt.*;

@Getter
@Setter
@ToString
public class SONG_YT_DTO {
    String name;
    String artist;
    String genre;
    String description;
    String url;
    String email;

}
