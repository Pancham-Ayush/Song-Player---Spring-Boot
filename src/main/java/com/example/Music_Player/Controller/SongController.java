package com.example.Music_Player.Controller;

import com.example.Music_Player.Model.Admin;
import com.example.Music_Player.Model.Song;
import com.example.Music_Player.Model.User;
import com.example.Music_Player.Repository.AdminRepo;
import com.example.Music_Player.Repository.PlaylistRepo;
import com.example.Music_Player.Repository.SongRepo;
import com.example.Music_Player.Repository.UserRepo;
import com.example.Music_Player.Service.DeleteService;
import com.example.Music_Player.Service.SongService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
public class SongController {
    @Autowired
    SongService songService;
    @Autowired
    SongRepo songRepo;
    @Autowired
    DeleteService deleteService;

    @Value("${song.stream.chunk-size}")
    Long chunkSize;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AdminRepo adminRepo;
    @Autowired
    private PlaylistRepo playlistRepo;

    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadSong(@RequestPart("file") MultipartFile file,
                                        @RequestPart("song") Song song,
                                        @RequestPart("mail")String mail
                                        ) throws IOException {
        if (!"audio/mpeg".equalsIgnoreCase(file.getContentType())) {
            return ResponseEntity.badRequest().body("Only MP3 files are allowed!");
        }
        Admin admin =adminRepo.findAdminByEmail(mail);
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
        Optional<Song> song = songRepo.findById(Long.parseLong(songid));
        if (song.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(song.get().getPath());
        long fileLength = path.toFile().length();

        long start = 0;
        long end = fileLength - 1;
//bytes=starng-endig
        if (range != null) {
            String[] ranges = range.replace("bytes=", "").split("-");
            start = Long.parseLong(ranges[0]);
            end = start+chunkSize;
            if(end > fileLength-1) {
                end = fileLength-1;
            }
        }
        System.out.println(" start - end  "+start+" "+end);
        try {
            InputStream inputStream = Files.newInputStream(path);
            inputStream.skip(start);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
            headers.add("Accept-Ranges", "bytes");
            headers.setContentLength(end - start + 1);

            // Detect MIME type (mp3, wav, etc.)
            String mimeType = Files.probeContentType(path);
            System.out.println(mimeType+" type");
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body(new InputStreamResource(inputStream));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/allsongs")
    public ResponseEntity<Page<Song>> getAllSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int chunk) {

        Pageable pageable = (Pageable) PageRequest.of(page, chunk);
        Page<Song> songs = songRepo.findAll(pageable);

        return ResponseEntity.ok(songs);
    }
    @GetMapping("/allsongs/delete")
    public ResponseEntity<?> getAllSongs(){

        LinkedList<Song> songs = new LinkedList<>(songRepo.findAll());

        return ResponseEntity.ok(Map.of("songs", songs));
    }
    @PostMapping("/delete")
    public ResponseEntity<?> deleteSong(@RequestBody Map<String, String> map) {
        Song song = songRepo.findById(Long.parseLong(map.get("id"))).orElse(null);
        if (song == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Message", "Song not found"));
        }
            Path path = Paths.get(song.getPath());
            path.toFile().delete();
            deleteService.deleteSong(Long.valueOf(map.get("id")));
            return ResponseEntity.ok().body(Map.of("Message", "Song deleted successfully!"));


    }

    @GetMapping("/autoplay")
    public ResponseEntity<?> autoPlaySong( ) {
        return ResponseEntity.ok(songRepo.findAll());
    }


}


