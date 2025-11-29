package com.example.SearchEngine_MicroService.Controller;

import com.example.SearchEngine_MicroService.Service.ElasticSearchService;
import com.example.SearchEngine_MicroService.Model.Song;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class SearchController {

    private final ElasticSearchService elasticSearchService;

    public SearchController(ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    @GetMapping("search")
    public ResponseEntity<List<Song>> search(@RequestParam String query) throws IOException {
        return ResponseEntity.ok(elasticSearchService.searchSongs(query));
    }
}
