
package com.example.Music_Player.Controller;

import com.example.Music_Player.DTO.SONG_YT_DTO;
import com.example.Music_Player.Model.Song;
import com.example.Music_Player.Model.YoutubeVideo;
import com.example.Music_Player.Service.AiService;
import com.example.Music_Player.Service.KafkaProducer;
import com.example.Music_Player.Service.SearchYT;
import com.example.Music_Player.Service.SongService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AIYTSongController {
    @Autowired
    SearchYT searchYT;
    @Autowired
    AiService aiService;
    @Autowired
    SongService songService;
    @Autowired
    KafkaProducer kafkaProducer;
    @GetMapping({"SearchOnYt"})
    public ResponseEntity<?> searchOnYt(@RequestParam String query, @RequestParam(required = false) String token) {
        if (query.startsWith("https://www.youtube.com/watch?v=")) {
            String id = query.replace("https://www.youtube.com/watch?v=", "");
            Map<String, Object> map = this.searchYT.search(id, (String)null);
            List<YoutubeVideo> ytList = (List)map.get("yt");
            YoutubeVideo youtubeVideo = (YoutubeVideo)ytList.get(0);
            Map<String, Object> response = new HashMap();
            response.put("yt", new ArrayList(List.of(youtubeVideo)));
            map.put("nextPageToken", (Object)null);
            map.put("prevPageToken", (Object)null);
            return new ResponseEntity(response, HttpStatus.OK);
        } else if (query.startsWith("https://youtu.be/")) {
            String id = query.replace("https://youtu.be/", "");
            if (id.contains("?")) {
                id = id.substring(0, id.indexOf(63));
            }

            Map<String, Object> map = this.searchYT.search(id, (String)null);
            List<YoutubeVideo> ytList = (List)map.get("yt");
            YoutubeVideo youtubeVideo = (YoutubeVideo)ytList.get(0);
            Map<String, Object> response = new HashMap();
            response.put("yt", new ArrayList(List.of(youtubeVideo)));
            response.put("nextPageToken", (Object)null);
            response.put("prevPageToken", (Object)null);
            return new ResponseEntity(response, HttpStatus.OK);
        } else {
            Map<String, Object> map = this.searchYT.search(query, token);
            return ResponseEntity.ok(map);
        }
    }

    @PostMapping({"AiDownloading"})
    public ResponseEntity<Object> download(@RequestBody YoutubeVideo youtubeVideo) {
        String ytDetail = youtubeVideo.toString();
        boolean check = this.aiService.AISongVerification(ytDetail);
        if (check) {
            SONG_YT_DTO obj = aiService.AiSongMapping(ytDetail);
            log.info(obj.toString());
            kafkaProducer.publishDownloadRequest(youtubeVideo);
            return ResponseEntity.ok("Under AI review for verification. Please check back in the Songs section within the next few min.");
        } else {
            return ResponseEntity.ok("Your song didn’t pass AI verification. Please check and upload again. ");
        }
    }
}
