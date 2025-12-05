package com.example.SearchEngine_MicroService.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.SearchEngine_MicroService.Model.Song;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ElasticSearchService {

    @Value("${elasticsearch.index}")
    private String indexName;

    private final ElasticsearchClient elasticsearchClient;

    public ElasticSearchService( ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public void uploadSong(Song song) throws IOException {
        IndexRequest<Song> indexRequest = IndexRequest.of(
                i -> i
                        .index(indexName)
                        .id(song.getId())
                        .document(song)
        );
        IndexResponse response = elasticsearchClient.index(indexRequest);
        log.debug(response.toString());
    }

    public List<Song> searchSongs(String query) throws IOException {

        MultiMatchQuery matchQuery = MultiMatchQuery.of( i -> i
                .query(query)
                .fuzziness("AUTO")
                .fuzzyTranspositions(true)
                .fields("title","description","name","artist","genre")
                .minimumShouldMatch("70%")
                .type(TextQueryType.BestFields));

        SearchRequest searchRequest = SearchRequest.of( s -> s
                .index(indexName)
                .query(matchQuery)
                .size(10));

        SearchResponse <Song>response = elasticsearchClient.search(searchRequest, Song.class);

        return response.hits().hits()
                .stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());

    }

}
