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

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

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

    }
    public CompletableFuture<Void> uploadYoutubeAudioAsync(String videoUrl, Song song) {
        return CompletableFuture.runAsync(() -> {
            String fileName = System.currentTimeMillis() + "_" +
                    song.getName().replaceAll("[^a-zA-Z0-9\\-_]", "_") + ".opus";
            String tempOutputPath = "/tmp/" + fileName;

            File downloadedFile = new File(tempOutputPath);

            try {
                // Download audio using yt-dlp
                ProcessBuilder pb = new ProcessBuilder(
                        "yt-dlp",
                        "-x",
                        "--audio-format", "opus",
                        "--audio-quality", "0",
                        "--prefer-ffmpeg",
                        "--force-overwrites",
                        "-o", tempOutputPath,
                        videoUrl
                );
                pb.redirectErrorStream(true);
                Process process = pb.start();

                // Consume process output to prevent blocking
                new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new IOException("YouTube download failed with exit code: " + exitCode);
                }

                if (!downloadedFile.exists() || downloadedFile.length() == 0) {
                    throw new IOException("Downloaded file not found or empty!");
                }

                // Upload to S3 asynchronously
                PutObjectRequest putRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .contentType("audio/opus")
                        .contentLength(downloadedFile.length())
                        .build();

                s3AsyncClient.putObject(putRequest, AsyncRequestBody.fromFile(downloadedFile))
                        .whenComplete((resp, err) -> {
                            try {
                                if (err != null) {
                                    // Upload failed → do NOT save in DB
                                    err.printStackTrace();
                                } else {
                                    // Upload succeeded → save song info in DB
                                    song.setPath(fileName);
                                    song.setSize(downloadedFile.length());
                                    songRepo.saveSong(song);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                // Delete temp file safely
                                try {
                                    Files.deleteIfExists(downloadedFile.toPath());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
                // Delete temp file if download failed
                try {
                    Files.deleteIfExists(downloadedFile.toPath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        });
    }


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
