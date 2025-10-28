package com.example.MIcroService_Player.Controller;

import com.example.MIcroService_Player.Model.Song;
import com.example.MIcroService_Player.Repo.SongRepo;
import com.example.MIcroService_Player.Service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.core.ResponseInputStream;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import java.io.IOException;

@RestController
public class SongController {
    @Autowired
    S3Client s3Client;
    @Autowired
    RedisService redisService;
    @Autowired
    SongRepo songRepo;
    @Value("${song.stream.chunk-size}")
    Long chunkSize;
    @Value("${aws.bucket}")
    String bucket;

    @GetMapping({"/get/{songid}"})
    public ResponseEntity<Resource> getSong(@PathVariable("songid") String songid, @RequestHeader(value = "Range",required = false) String range) throws IOException {
        Song song = redisService.get(songid);
        if (song != null) {
            System.out.println("Getting song from cache: " + song.getName());
        } else {
            song = (Song)this.songRepo.findById(songid).orElseThrow();
            redisService.set(songid, song);
            System.out.println("Getting song from DB: " + song.getName());
        }


        String path = song.getPath();
        long fileLength = song.getSize();
        long start = 0L;
        GetObjectRequest.Builder headRequest = GetObjectRequest.builder().bucket(this.bucket).key(path);
        long end;
        if (range != null) {
            System.out.println(range);
            String[] rangeStart = range.replace("bytes=", "").split("-");
            start = Long.parseLong(rangeStart[0]);
            end = Long.parseLong(rangeStart[0]) + this.chunkSize;
            if (end > fileLength) {
                end = fileLength - 1L;
            }
        } else {
            end = this.chunkSize;
        }


        headRequest.range("bytes=" + start + "-" + end);
        ResponseInputStream<GetObjectResponse> responseInputStream = this.s3Client.getObject((GetObjectRequest)headRequest.build());
        GetObjectResponse response = (GetObjectResponse)responseInputStream.response();
        System.out.println(" start - end  " + start + " " + end);


        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
            headers.add("Accept-Ranges", "bytes");
            headers.setContentLength(end - start + 1L);
            String mimeType = response.contentType();
            System.out.println(mimeType + " type");
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }


            return ((ResponseEntity.BodyBuilder)ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(headers)).contentType(MediaType.parseMediaType(mimeType)).body(new InputStreamResource(responseInputStream));
        } catch (Exception var16) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
