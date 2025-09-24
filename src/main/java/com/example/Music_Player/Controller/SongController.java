package com.example.Music_Player.Controller;

import com.example.Music_Player.Model.Admin;
import com.example.Music_Player.Model.Song;
import com.example.Music_Player.Repository.AdminRepo;
import com.example.Music_Player.Repository.PlaylistRepo;
import com.example.Music_Player.Repository.SongRepo;
import com.example.Music_Player.Repository.UserRepo;
import com.example.Music_Player.Service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;


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
    static List<String > SongContentType = List.of("audio/ogg","audio/mpeg");

    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadSong(@RequestPart("file") MultipartFile file,
                                        @RequestPart("song") Song song,
                                        @RequestPart("mail")String mail
                                        ) throws IOException {
        if (!SongContentType.contains(file.getContentType().toLowerCase())) {
            return ResponseEntity.badRequest().body("Only MP3 / OGG files are allowed!");
        }
        Admin admin =adminRepo.getAdminByEmail(mail);
        if (admin != null) {
            Song saved = songService.addSong(song, file);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Song saved successfully!");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }




    @GetMapping("/get/{songid}")
    public ResponseEntity<Resource> getSong(@PathVariable("songid") String songid,
                                            @RequestHeader(value = "Range", required = false) String range) {
        Optional<Song> song = songRepo.findById(songid);
        if (song.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String path =song.get().getPath();
        long fileLength=song.get().getSize();
        long start = 0, end ;


        GetObjectRequest.Builder headRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(path);
//bytes=starting-ending

        if (range != null) {
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
            @RequestParam(defaultValue = "20") int chunk) {

        List<Song> songs = songRepo.findAll(page, chunk);

        Map<String, Object> response = new HashMap<>();
        response.put("content", songs); // actual songs array
        response.put("totalPages", -1); // Indicate not easily available
        response.put("currentPage", page);
        response.put("totalElements", -1); // Indicate not easily available

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


