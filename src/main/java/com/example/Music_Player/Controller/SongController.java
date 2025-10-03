package com.example.Music_Player.Controller;

import com.example.Music_Player.Model.Admin;
import com.example.Music_Player.Model.CacheMap;
import com.example.Music_Player.Model.Song;
import com.example.Music_Player.Repository.AdminRepo;
import com.example.Music_Player.Repository.PlaylistRepo;
import com.example.Music_Player.Repository.SongRepo;
import com.example.Music_Player.Repository.UserRepo;
import com.example.Music_Player.Service.SongService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.util.*;


@RestController
public class SongController {
    @Autowired
    SongService songService;
    @Autowired
    SongRepo songRepo;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AdminRepo adminRepo;
    @Autowired
    private PlaylistRepo playlistRepo;
    @Autowired
    S3Client s3Client;
    @Value("${song.stream.chunk-size}")
    Long chunkSize;
    @Value("${aws.bucket}")
    String bucket;
    @Value("${Song.Cache}")
    private int songCacheSize;

    private Map<String, Song> songCacheTable;

    @PostConstruct
    public void initCache() {
        songCacheTable = new CacheMap<>(songCacheSize);
    }

    private static final List<String> SongContentType = List.of(
            "audio/mpeg",  // MP3
            "audio/ogg",   // OGG
            "audio/opus"   // OPUS codec
    );






    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadSong(@RequestPart(value = "file" , required = false) MultipartFile file,
                                        @RequestPart(value = "ytUrl" , required = false) String ytUrl,
                                        @RequestPart("song") Song song,
                                        @RequestPart("mail")String mail
                                        ) throws IOException, InterruptedException {
        if (file!=null && !SongContentType.contains(file.getContentType().toLowerCase())) {
            return ResponseEntity.badRequest().body("Only MP3 / OGG files are allowed!");
        }
        else
            System.out.println(ytUrl);
        Admin admin =adminRepo.getAdminByEmail(mail);
        if (admin != null) {
            if(file!=null) {
                Song saved = songService.addSong(song, file);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Song saved successfully!");
                return ResponseEntity.ok(response);
            }
            else {
                songService.uploadYoutubeAudioAsync(ytUrl,song);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Song saved successfully!");
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }




    @GetMapping("/get/{songid}")
    public ResponseEntity<Resource> getSong(@PathVariable("songid") String songid,
                                            @RequestHeader(value = "Range", required = false) String range) throws IOException {

        // 1️⃣ Fetch song metadata (cache first)
        Song song;
        if (songCacheTable.containsKey(songid)) {
            song = songCacheTable.get(songid);
            System.out.println("Getting song from cache: " + song.getName());
        } else {
            song = songRepo.findById(songid).orElseThrow();
            songCacheTable.put(songid, song);
            System.out.println("Getting song from DB: " + song.getName());
        }
        String path =song.getPath();
        long fileLength=song.getSize();
        long start = 0, end ;


        GetObjectRequest.Builder headRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(path);
//bytes=starting-ending

        if (range != null) {
            System.out.println(range);
            String rangeStart[] = range.replace("bytes=", "").split("-");
            start=Long.parseLong(rangeStart[0]);
            end = Long.parseLong(rangeStart[0])+chunkSize;
            if (end > fileLength)
                end=fileLength-1;
        }
        else
        {
            end = chunkSize;
        }
        headRequest.range("bytes=" + start+"-"+end);

        ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(headRequest.build());
        GetObjectResponse response = responseInputStream.response();

        System.out.println(" start - end  "+start+" "+end);
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
            headers.add("Accept-Ranges", "bytes");
            headers.setContentLength(end - start + 1);

            // Detect MIME type (mp3, wav, etc.)
            String mimeType = response.contentType();
            System.out.println(mimeType+" type");
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body(new InputStreamResource(responseInputStream));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @GetMapping("/allsongs")
    public ResponseEntity<Map<String, Object>> getAllSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int chunk) {

            Map<String,Object> response = songRepo.findAll(page, chunk);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/allsongs/delete")
    public ResponseEntity<?> getAllSongsForDelete(){

        List<Song> songs = songRepo.findAll();

        return ResponseEntity.ok(Map.of("songs", songs));
    }
    @PostMapping("/delete")
    public ResponseEntity<?> deleteSong(@RequestBody Map<String, String> map) {
        Song song = songRepo.findById(map.get("id")).orElse(null);
        if (song == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Message", "Song not found"));
        }
        String key = song.getPath();
            songRepo.deleteSong(song.getId());
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());

            return ResponseEntity.ok().body(Map.of("Message", "Song deleted successfully!"));


    }

    @GetMapping("/autoplay")
    public ResponseEntity<?> autoPlaySong( ) {
        return ResponseEntity.ok(songRepo.findAll());
    }


}


