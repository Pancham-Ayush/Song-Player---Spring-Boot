package com.example.YT_S3_MicroService.Controller;

import com.example.YT_S3_MicroService.Model.Song;
import com.example.YT_S3_MicroService.Service.YT_DLP_Service;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
public class ManualUploadController {

    private static final List<String> SongContentType = List.of("audio/mpeg", "audio/ogg", "audio/opus");
    private final YT_DLP_Service yt_dlp_service;

    public ManualUploadController(YT_DLP_Service yt_dlp_service) {
        this.yt_dlp_service = yt_dlp_service;
    }

    @PostMapping({"/upload"})
    public ResponseEntity<Object> uploadSong(@RequestPart(value = "file", required = false) MultipartFile file, @RequestPart(value = "ytUrl", required = false) String ytUrl, @RequestPart("song") Song song, HttpServletRequest request) throws IOException, InterruptedException, ExecutionException {

        if (file != null && !SongContentType.contains(file.getContentType().toLowerCase())) {
            return ResponseEntity.badRequest().body("Only MP3 / OGG files are allowed!");
        } else {
            if (file != null) {
                this.yt_dlp_service.manualAdd(song, file);
                Map<String, String> response = new HashMap();
                response.put("message", "Song saved successfully!");
                return ResponseEntity.ok(response);
            } else {
                this.yt_dlp_service.uploadYoutubeAudioAsync(ytUrl, song, request.getParameter("X-User-Email"));
                Map<String, String> response = new HashMap();
                response.put("message", "Song saved successfully!");
                return ResponseEntity.ok(response);
            }
        }

    }
}
