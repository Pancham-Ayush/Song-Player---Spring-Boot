package com.example.SearchEngine_MicroService.Model;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Song {

    String id ;
    String name;
    String artist;
    String genre;
    String description;
    String path;
    Long size;

}
