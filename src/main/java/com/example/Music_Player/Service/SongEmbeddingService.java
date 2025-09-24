package com.example.Music_Player.Service;

import com.example.Music_Player.Model.Song;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.ai.document.Document;
import java.util.List;
import java.util.Map;

@Service
public class SongEmbeddingService
{
    @Autowired
    VectorStore vectorStore;
    @Async
    public void addSongs(Song song)  {
        Map<?,Object> meta = Map.of("Song Name",song.getName(),"Song Artist",song.getArtist(),"Song Genre",song.getGenre(),"Song Description",song.getDescription());
        Document document = new Document(song.getId(), (Map<String, Object>) meta) ;
        vectorStore.add(List.of(document));
    }

    public List<Document> searchSongs(String query) {
        return vectorStore.similaritySearch(SearchRequest.builder()
                .query(query).build());

    }


}
