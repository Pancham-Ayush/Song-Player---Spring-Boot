package com.example.SearchEngine_MicroService.Controller;

import com.example.SearchEngine_MicroService.Model.Song;
import com.example.SearchEngine_MicroService.Repo.PlaylistRepo;
import com.example.SearchEngine_MicroService.Repo.SongRepo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@RestController
public class DeleteSongController {

    private final Executor virtualThreadExecutor;

    private final SongRepo songRepo;

    private final PlaylistRepo playlistRepo;

    private final S3Client s3Client;

    DeleteSongController (@Qualifier("Virtual") Executor virtualThreadExecutor, SongRepo songRepo, PlaylistRepo playlistRepo, S3Client s3Client) {
        this.virtualThreadExecutor = virtualThreadExecutor;
        this.songRepo = songRepo;
        this.playlistRepo = playlistRepo;
        this.s3Client = s3Client;
    }

    @Value("${aws.bucket}")
    private String bucketName;

    @PostMapping({"/delete"})
    public ResponseEntity<Map<String, String>> deleteSong(@RequestBody Map<String, String> map) throws ExecutionException, InterruptedException {
        Future<ResponseEntity<Map<String, String>>> future =
                ((ExecutorService) virtualThreadExecutor)
                        .submit(() -> {
                            Song song = (Song) this.songRepo.findById((String) map.get("id")).orElse(null);
                            if (song == null) {
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Message", "Song not found"));
                            } else {
                                String key = song.getPath();
                                this.songRepo.deleteSong(song.getId());
                                this.playlistRepo.deleteFromPlaylist(song.getId());
                                this.s3Client.deleteObject( DeleteObjectRequest.builder().bucket(this.bucketName).key(key).build());
                                return ResponseEntity.ok().body(Map.of("Message", "Song deleted successfully!"));
                            }
                        });
        return future.get();
    }
}
