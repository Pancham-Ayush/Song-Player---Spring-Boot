package com.example.YT_S3_MicroService.Controller;

import com.example.YT_S3_MicroService.ServerSentEvent.SSE_Service;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notification")
public class SSE_Controller {
    private final SSE_Service sseService;

    public SSE_Controller(SSE_Service sseService) {
        this.sseService = sseService;
    }

    @GetMapping("/stream")
    SseEmitter getSseEmitter(HttpServletRequest request) {
        return sseService.addUser(request);
    }
}
