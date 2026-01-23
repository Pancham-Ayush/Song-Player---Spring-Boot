package com.example.SearchEngine_MicroService.GraphQl.GraphQL_DTO;

import com.example.SearchEngine_MicroService.Model.Song;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class SongPage {
    private List<Song> content;
    private int chunkSize;
    private String nextCursor;
}
