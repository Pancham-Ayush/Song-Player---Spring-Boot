

package com.example.Music_Player.Controller;

import com.example.Music_Player.Model.Song;
import com.example.Music_Player.AI.SongEmbeddingService;
import io.pinecone.clients.Index;
import io.pinecone.clients.Pinecone;
import io.pinecone.configs.PineconeConfig;
import io.pinecone.configs.PineconeConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openapitools.db_data.client.model.Hit;
import org.openapitools.db_data.client.model.SearchRecordsRequestRerank;
import org.openapitools.db_data.client.model.SearchRecordsResponse;
import org.springframework.ai.mistralai.MistralAiEmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.VectorStoreRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchSongController {
    @Autowired
    SongEmbeddingService songEmbeddingService;
    @Autowired
    Pinecone pinecone;
    @Autowired
    @Qualifier("vector")
    VectorStore vectorStore;
    @Qualifier("vector")
    @Autowired
    VectorStoreRetriever retriever;
    @Autowired
    MistralAiEmbeddingModel mistralAiEmbeddingModel;
    @Autowired
    PineconeConfig pineconeConfig;
    @Autowired
    PineconeConnection pineconeConnection;
    @Value("${spring.ai.vectorstore.pinecone.index-name}")
    private String INDEX_NAME;
    private final String NAMESPACE = "__default__";
    private final int TOP_K = 10;

    @GetMapping({"CosineSearch"})
    public List<Song> searchCosineSong(@RequestParam String query) {
        List<Song> songs = new ArrayList();
        Long a = System.currentTimeMillis();

        try {
            Index index = new Index(this.pineconeConfig, this.pineconeConnection, this.INDEX_NAME);
            SearchRecordsResponse response = index.searchRecordsByText(query, "__default__", List.of("id", "name", "artist", "genre", "description"), 10, (Map)null, (SearchRecordsRequestRerank)null);
            if (response != null && response.getResult() != null && response.getResult().getHits() != null) {
                for(Hit hit : response.getResult().getHits()) {
                    Map<String, Object> fields = (Map)hit.getFields();
                    Song song = new Song();
                    song.setId((String)fields.get("id"));
                    song.setName((String)fields.get("name"));
                    song.setArtist((String)fields.get("artist"));
                    song.setGenre((String)fields.get("genre"));
                    song.setDescription((String)fields.get("description"));
                    songs.add(song);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return songs;
    }
}
