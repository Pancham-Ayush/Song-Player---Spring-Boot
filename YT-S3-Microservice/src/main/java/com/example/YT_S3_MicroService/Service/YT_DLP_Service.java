package com.example.YT_S3_MicroService.Service;

import com.example.YT_S3_MicroService.Kafka.KafkaElasticSearchAdditionReq;
import com.example.YT_S3_MicroService.Model.Song;
import com.example.YT_S3_MicroService.Repository.SongRepo;
import com.example.YT_S3_MicroService.ServerSentEvent.SSE_Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class YT_DLP_Service {

    private final SongRepo songRepo;
    private final S3AsyncClient s3AsyncClient;
    private final KafkaElasticSearchAdditionReq kafkaElasticSearchAdditionReq;
    private final ObjectMapper objectMapper;
    private final SSE_Service sseService;
    @Value("${aws.bucket}")
    String bucket;
    @Value("${file.upload-dir}")
    private String uploadDir;

    public YT_DLP_Service(SongRepo songRepo, S3AsyncClient s3AsyncClient, KafkaElasticSearchAdditionReq kafkaElasticSearchAdditionReq, ObjectMapper objectMapper, SSE_Service sseService) {
        this.songRepo = songRepo;
        this.s3AsyncClient = s3AsyncClient;
        this.kafkaElasticSearchAdditionReq = kafkaElasticSearchAdditionReq;
        this.objectMapper = objectMapper;
        this.sseService = sseService;
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

    public CompletableFuture<Void> uploadYoutubeAudioAsync(String videoUrl, Song song, String Email) {
        return CompletableFuture.runAsync(() -> {

            String fileName = System.currentTimeMillis() + "_" + song.getName().replaceAll("[^a-zA-Z0-9\\-_]", "_") + ".opus";
            String tempOutputPath = "/tmp/" + fileName;
            File downloadedFile = new File(tempOutputPath);

            try {
                Process process = getProcess(videoUrl, tempOutputPath);
                (new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        while (reader.readLine() != null) {
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                })).start();
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    log.error("YouTube download failed with exit code: {}", exitCode);
                    Files.deleteIfExists(downloadedFile.toPath());

                }

                if (!downloadedFile.exists() || downloadedFile.length() == 0L) {
                    log.error("Downloaded file not found or empty!");
                    Files.deleteIfExists(downloadedFile.toPath());

                }

                PutObjectRequest putRequest = (PutObjectRequest) PutObjectRequest.builder().bucket(this.bucket)
                        .key(fileName)
                        .contentType("audio/opus")
                        .contentLength(downloadedFile.length())
                        .build();
                this.s3AsyncClient.putObject(putRequest, AsyncRequestBody.fromFile(downloadedFile)).whenComplete((resp, err) -> {
                    try {
                        if (err != null) {
                            err.printStackTrace();
                        } else {
                            song.setPath(fileName);
                            song.setSize(downloadedFile.length());
                            this.songRepo.saveSong(song);
                            String openSearchSong_Json = objectMapper.writeValueAsString(song);
                            kafkaElasticSearchAdditionReq.sendMessage(openSearchSong_Json);
                            log.info("Successfully uploaded Youtube Audio File");
                            sseService.sendUser(Email, song);
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

    CompletableFuture<PutObjectResponse> UploadASYNC(String path, MultipartFile file) throws IOException {
        PutObjectRequest putObjectRequest = (PutObjectRequest) PutObjectRequest.builder().bucket(this.bucket).key(path).contentType(file.getContentType()).build();
        return this.s3AsyncClient.putObject(putObjectRequest, AsyncRequestBody.fromBytes(file.getBytes()));
    }

    public Song manualAdd(Song song, MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + Math.random() + "_" + file.getOriginalFilename();
            song.setPath(fileName);
            song.setSize(file.getSize());
            this.UploadASYNC(fileName, file);
            String openSearchSong_Json = objectMapper.writeValueAsString(song);
            kafkaElasticSearchAdditionReq.sendMessage(openSearchSong_Json);
            return this.songRepo.saveSong(song);
        } catch (Exception var4) {
            return null;
        }
    }

}
