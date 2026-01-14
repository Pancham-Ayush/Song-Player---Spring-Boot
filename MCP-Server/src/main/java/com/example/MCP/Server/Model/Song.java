package com.example.MCP.Server.Model;


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
