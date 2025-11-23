
package com.example.Music_Player.Service;

import com.example.Music_Player.AI.SongEmbeddingService;
import com.example.Music_Player.Model.Song;
import com.example.Music_Player.Repository.SongRepo;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class SongService {
    @Value("${file.upload-dir}")
    private String uploadDir;
    @Value("${aws.bucket}")
    String bucket;

    private final SongRepo songRepo;

    private final S3AsyncClient s3AsyncClient;
    private final SongEmbeddingService songEmbeddingService;

    public SongService(SongRepo songRepo, S3AsyncClient s3AsyncClient,SongEmbeddingService songEmbeddingService) {
        this.songRepo = songRepo;
        this.s3AsyncClient = s3AsyncClient;
        this.songEmbeddingService = songEmbeddingService;
    }

    CompletableFuture<PutObjectResponse> UploadASYNC(String path, MultipartFile file) throws IOException {
        PutObjectRequest putObjectRequest = (PutObjectRequest)PutObjectRequest.builder().bucket(this.bucket).key(path).contentType(file.getContentType()).build();
        return this.s3AsyncClient.putObject(putObjectRequest, AsyncRequestBody.fromBytes(file.getBytes()));
    }

    public CompletableFuture<Void> uploadYoutubeAudioAsync(String videoUrl, Song song) {
        return CompletableFuture.runAsync(() -> {
            long var10000 = System.currentTimeMillis();
            String fileName = var10000 + "_" + song.getName().replaceAll("[^a-zA-Z0-9\\-_]", "_") + ".opus";
            String tempOutputPath = "/tmp/" + fileName;
            File downloadedFile = new File(tempOutputPath);

            try {
                Process process = getProcess(videoUrl, tempOutputPath);
                (new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        while(reader.readLine() != null) {
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                })).start();
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new IOException("YouTube download failed with exit code: " + exitCode);
                }

                if (!downloadedFile.exists() || downloadedFile.length() == 0L) {
                    throw new IOException("Downloaded file not found or empty!");
                }

                PutObjectRequest putRequest = (PutObjectRequest)PutObjectRequest.builder().bucket(this.bucket).key(fileName).contentType("audio/opus").contentLength(downloadedFile.length()).build();
                this.s3AsyncClient.putObject(putRequest, AsyncRequestBody.fromFile(downloadedFile)).whenComplete((resp, err) -> {
                    try {
                        if (err != null) {
                            err.printStackTrace();
                        } else {
                            song.setPath(fileName);
                            song.setSize(downloadedFile.length());
                            this.songRepo.saveSong(song);
                            this.songEmbeddingService.addSongs(song);
                            System.out.println("embed");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            Files.deleteIfExists(downloadedFile.toPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                });
            } catch (Exception e) {
                e.printStackTrace();

                try {
                    Files.deleteIfExists(downloadedFile.toPath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        });
    }

    private static Process getProcess(String videoUrl, String tempOutputPath) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "yt-dlp",
                "-x", "--audio-format", "opus",
                "--audio-quality", "0",
                "--prefer-ffmpeg",
                "--ffmpeg-location", "/usr/bin/ffmpeg",
                "--force-overwrites",
                "-o", tempOutputPath,
                videoUrl
        );
        pb.redirectErrorStream(true);
        return pb.start();
    }


    public Song addSong(Song song, MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis()+Math.random()+ "_" + file.getOriginalFilename();
            song.setPath(fileName);
            song.setSize(file.getSize());
            this.UploadASYNC(fileName, file);
            this.songEmbeddingService.addSongs(song);
            return this.songRepo.saveSong(song);
        } catch (Exception var4) {
            return null;
        }
    }
}