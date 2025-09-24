package com.example.Music_Player.Service;

import com.example.Music_Player.Model.Song;
import com.example.Music_Player.Repository.SongRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
public class SongService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public SongRepo songRepo;

    @Value("${aws.bucket}")
    String bucket;
    @Autowired
    S3AsyncClient  s3AsyncClient;

    CompletableFuture<PutObjectResponse> UploadASYNC(String path ,MultipartFile file) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(path)
                .contentType(file.getContentType())
                .build();

        return s3AsyncClient.putObject(putObjectRequest,AsyncRequestBody.fromBytes(file.getBytes()));

    };


    public Song addSong(Song song, MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            song.setPath(fileName);
            song.setSize(file.getSize());
            UploadASYNC(fileName, file);
            return songRepo.saveSong(song);
        } catch (Exception e) {
            return null;
        }
    }






}
