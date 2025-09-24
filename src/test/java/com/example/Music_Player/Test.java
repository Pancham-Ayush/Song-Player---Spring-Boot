package com.example.Music_Player;

import com.example.Music_Player.Repository.SongRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Test
{
    @Autowired
    SongRepo songRepo;

    @org.junit.jupiter.api.Test
    public void test() {
        songRepo.findAll(0, 20).forEach(x ->
                System.out.println(
                        x.getId() + " jj " +
                                x.getName() + " " +
                                x.getArtist() + " " +
                                x.getGenre() + " " +
                                x.getDescription()
                )
        );
    }

}
