package com.example.MIcroService_Player.Service;

import com.example.MIcroService_Player.Model.Song;
import com.example.MIcroService_Player.Repo.SongRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;

@Slf4j
@Service
public class RetrievalService {
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


    public ResponseEntity<Resource> getSong(@PathVariable("songid") String songid, @RequestHeader(value = "Range", required = false) String range) throws IOException {
        Long cur = System.currentTimeMillis();
        Song song = redisService.get(songid);
        if (song != null) {
            log.info("From Redis Cache");
        } else {
            song = (Song) this.songRepo.findById(songid).orElseThrow();
            redisService.set(songid, song);
            log.info("From Database");
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
            end = (end > fileLength) ? fileLength - 1L : end;


            headRequest.range("bytes=" + start + "-" + end);
            ResponseInputStream<GetObjectResponse> responseInputStream = this.s3Client.getObject((GetObjectRequest) headRequest.build());
            GetObjectResponse response = (GetObjectResponse) responseInputStream.response();
            log.info(" start - end  {} {}", start, end);

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
                log.info("Time :" + (System.currentTimeMillis()-cur));

                return ((ResponseEntity.BodyBuilder) ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(headers)).contentType(MediaType.parseMediaType(mimeType)).body(new InputStreamResource(responseInputStream));
            } catch (Exception var16) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.notFound().build();
    }
}
