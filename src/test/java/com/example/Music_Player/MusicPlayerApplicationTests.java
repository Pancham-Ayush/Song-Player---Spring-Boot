package com.example.Music_Player;

import com.example.Music_Player.Service.SongEmbeddingService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MusicPlayerApplicationTests {
@Autowired
    SongEmbeddingService songEmbeddingService;
	@Test
	void contextLoads() throws InterruptedException {
//        songEmbeddingService.addSongs();
        for (Document heartBroken : songEmbeddingService.searchSongs("heart broken")) {
            System.out.println(heartBroken);
        }

    }

}
