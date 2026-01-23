package com.example.SearchEngine_MicroService;

import com.example.SearchEngine_MicroService.GraphQl.GraphQL_DTO.SongPage;
import com.example.SearchEngine_MicroService.Repo.SongRepo;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class SongQueryResolver {

    private final SongRepo songService;

    public SongQueryResolver(SongRepo songService) {
        this.songService = songService;
    }

    @QueryMapping
    public SongPage getAllSongs(
            @Argument String cursor,
            @Argument int chunkSize
    ) {
        return songService.findAll(cursor, chunkSize);
    }
}
