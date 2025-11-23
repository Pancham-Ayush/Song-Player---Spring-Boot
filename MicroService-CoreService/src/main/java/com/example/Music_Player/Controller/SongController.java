package com.example.Music_Player.Controller;
import com.example.Music_Player.Feign.SongPlayerClient;
import com.example.Music_Player.Model.Song;
import com.example.Music_Player.Repository.PlaylistRepo;
import com.example.Music_Player.Repository.SongRepo;
import com.example.Music_Player.Redis.RedisService;
import com.example.Music_Player.Service.SongService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
@RestController
public class SongController {
    @Autowired
    SongService songService;
    @Autowired
    SongRepo songRepo;
    @Autowired
    private PlaylistRepo playlistRepo;
    @Autowired
    S3Client s3Client;
    @Autowired
    @Qualifier("vector")
    VectorStore vectorStore;
    @Autowired
    RedisService redisService;
    @Autowired
    SongPlayerClient songPlayerClient;

    @Value("${aws.bucket}")
    String bucket;
    private Map<String, Song> songCacheTable;
    private static final List<String> SongContentType = List.of("audio/mpeg", "audio/ogg", "audio/opus");

    @PostMapping({"/upload"})
    public ResponseEntity<?> uploadSong(@RequestPart(value = "file",required = false) MultipartFile file, @RequestPart(value = "ytUrl",required = false) String ytUrl, @RequestPart("song") Song song, @RequestPart("mail") String mail) throws IOException, InterruptedException {
        if (file != null && !SongContentType.contains(file.getContentType().toLowerCase())) {
            return ResponseEntity.badRequest().body("Only MP3 / OGG files are allowed!");
        } else {
            System.out.println(ytUrl);

            if (file != null) {
                this.songService.addSong(song, file);
                Map<String, String> response = new HashMap();
                response.put("message", "Song saved successfully!");
                return ResponseEntity.ok(response);
            } else {
                this.songService.uploadYoutubeAudioAsync(ytUrl, song);
                Map<String, String> response = new HashMap();
                response.put("message", "Song saved successfully!");
                return ResponseEntity.ok(response);
            }
        }
    }

@GetMapping({"/get/{songid}"})
public ResponseEntity<Resource> getSong(@PathVariable("songid") String songid, @RequestHeader(value = "Range",required = false) String range) throws IOException {
    return songPlayerClient.getSong(songid,range);
}

@GetMapping({"/allsongs"})
public ResponseEntity<Map<String, Object>> getAllSongs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int chunk) {
    Map<String, Object> response = this.songRepo.findAll(page, chunk);
    return ResponseEntity.ok(response);
}

@GetMapping({"/allsongs/delete"})
public ResponseEntity<?> getAllSongsForDelete() {
    List<Song> songs = this.songRepo.findAll();
    return ResponseEntity.ok(Map.of("songs", songs));
}

@PostMapping({"/delete"})
public ResponseEntity<?> deleteSong(@RequestBody Map<String, String> map) {
    Song song = (Song)this.songRepo.findById((String)map.get("id")).orElse(null);
    if (song == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Message", "Song not found"));
    } else {
        String key = song.getPath();
        this.songRepo.deleteSong(song.getId());
        this.playlistRepo.deleteFromPlaylist(song.getId());
        this.s3Client.deleteObject((DeleteObjectRequest)DeleteObjectRequest.builder().bucket(this.bucket).key(key).build());

        try {
            this.vectorStore.delete(List.of(song.getId()));
        } catch (Exception var5) {
        }

        return ResponseEntity.ok().body(Map.of("Message", "Song deleted successfully!"));
    }
}

@GetMapping({"/autoplay"})
public ResponseEntity<?> autoPlaySong() {
    return ResponseEntity.ok(this.songRepo.findAll());
}
}
