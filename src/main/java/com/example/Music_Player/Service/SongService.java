package com.example.Music_Player.Service;

import com.example.Music_Player.Model.Song;
import com.example.Music_Player.Repository.SongRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class SongService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public SongRepo songRepo;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("Upload dir created at: " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Song addSong(Song song, MultipartFile file) {
        try {
            Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(path); // make sure folder exists

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = path.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            song.setPath(filePath.toString());
            return songRepo.saveSong(song);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // or throw custom exception
        }
    }




}
