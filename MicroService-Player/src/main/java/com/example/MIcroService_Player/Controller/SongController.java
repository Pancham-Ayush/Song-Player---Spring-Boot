package com.example.MIcroService_Player.Controller;

import com.example.MIcroService_Player.Model.Song;
import com.example.MIcroService_Player.Repo.SongRepo;
import com.example.MIcroService_Player.Service.RedisService;
import com.example.MIcroService_Player.Service.RetrievalService;
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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.ResponseInputStream;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.springframework.core.io.Resource;

@RestController
public class SongController {

    private final RetrievalService retrievalService;

    private final Executor virtualThreadExecutor;


    public SongController(RetrievalService retrievalService, Executor VirtualThreadExecutor) {
        this.retrievalService = retrievalService;
        this.virtualThreadExecutor = VirtualThreadExecutor;
    }

    @GetMapping({"/get/{songid}"})
    public ResponseEntity<Resource> getSong(@PathVariable("songid") String songid, @RequestHeader(value = "Range",required = false) String range) throws IOException, ExecutionException, InterruptedException {
        Future<ResponseEntity<Resource>> future = ((ExecutorService) virtualThreadExecutor)
                .submit(() -> retrievalService.getSong(songid, range));

        return future.get();
    }
}
