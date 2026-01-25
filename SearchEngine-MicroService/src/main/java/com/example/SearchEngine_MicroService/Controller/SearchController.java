package com.example.SearchEngine_MicroService.Controller;

import com.example.SearchEngine_MicroService.Model.Song;
import com.example.SearchEngine_MicroService.Service.ElasticSearchService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@Validated
public class SearchController {

    private final ElasticSearchService elasticSearchService;

    public SearchController(ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    @GetMapping("search")
    public ResponseEntity<List<Song>> search(@RequestParam @NotBlank String query) throws IOException {
        return ResponseEntity.ok(elasticSearchService.searchSongs(query));
    }
}
