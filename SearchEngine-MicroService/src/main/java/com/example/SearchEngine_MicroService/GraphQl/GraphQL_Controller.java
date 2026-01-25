package com.example.SearchEngine_MicroService.GraphQl;

import com.example.SearchEngine_MicroService.GraphQl.GraphQL_DTO.SongPage;
import com.example.SearchEngine_MicroService.Repo.SongRepo;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GraphQL_Controller {

    private final SongRepo songRepo;

    public GraphQL_Controller(SongRepo songRepo) {
        this.songRepo = songRepo;
    }

    @QueryMapping
    public SongPage getAllSongs(@Argument String cursor, @Argument Integer chunkSize) {
        return songRepo.findAll(cursor, chunkSize);
    }
}
