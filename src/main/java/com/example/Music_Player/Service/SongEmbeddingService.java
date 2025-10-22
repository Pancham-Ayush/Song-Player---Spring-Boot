

package com.example.Music_Player.Service;

import com.example.Music_Player.Model.Song;
import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SongEmbeddingService {
    @Autowired
    @Qualifier("vector")
    VectorStore vectorStore;

    @Async
    public void addSongs(Song song) {
        String var10000 = song.getName();
        String contentToEmbed = "Name: " + var10000 + " Artist: " + song.getArtist() + " Genre: " + song.getGenre() + " Description: " + song.getDescription();
        Map<String, Object> metadata = Map.of("id", song.getId(), "name", song.getName(), "artist", song.getArtist(), "genre", song.getGenre(), "description", song.getDescription());
        Document document = (new Document.Builder()).id(song.getId()).text(contentToEmbed).metadata(metadata).build();
        this.vectorStore.add(List.of(document));
    }

    public List<Document> searchSongs(String query) {
        return this.vectorStore.similaritySearch(SearchRequest.builder().query(query).topK(2).build());
    }
}
