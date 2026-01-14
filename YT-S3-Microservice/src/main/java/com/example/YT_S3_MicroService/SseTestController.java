package com.example.YT_S3_MicroService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/test")
public class SseTestController {

    @GetMapping("/stream")
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(0L); // no timeout

        Thread t = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(i * 100);
                    emitter.send("mess" + i);

                }
                emitter.complete();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
            t.start();

            return emitter;
    }
}
