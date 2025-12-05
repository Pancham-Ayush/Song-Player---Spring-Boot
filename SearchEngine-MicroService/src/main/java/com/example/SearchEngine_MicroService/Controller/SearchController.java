package com.example.SearchEngine_MicroService.Controller;

import com.example.SearchEngine_MicroService.Repo.SongRepo;
import com.example.SearchEngine_MicroService.Service.ElasticSearchService;
import com.example.SearchEngine_MicroService.Model.Song;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@RestController
public class SearchController {

    private final ElasticSearchService elasticSearchService;

    private final SongRepo  songRepo;

    private final Executor virtualThreadExecutor;

    public SearchController(ElasticSearchService elasticSearchService, SongRepo  songRepo, @Qualifier("Virtual") Executor virtualThreadExecutor) {
        this.elasticSearchService = elasticSearchService;
        this.songRepo = songRepo;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    @GetMapping("search")
    public ResponseEntity<List<Song>> search(@RequestParam String query) throws IOException {
        return ResponseEntity.ok(elasticSearchService.searchSongs(query));
    }
    @GetMapping({"/allsongs"})
    public ResponseEntity<Map<String, Object>> getAllSongs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int chunk) throws ExecutionException, InterruptedException {
        Future<ResponseEntity<Map<String, Object>>> future = ((ExecutorService) virtualThreadExecutor)
                .submit(() -> ResponseEntity.ok(songRepo.findAll(page, chunk)));
        return future.get();
    }
}
