package com.example.YT_S3_MicroService.ServerSentEvent;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SSE_Service {

    final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    @Value("${JWT.secret.key}")
    String secretKey;

    public SseEmitter addUser(HttpServletRequest request) {
        String email = request.getHeader("X-User-Email");
    System.out.println("email++++++: " + email);
        SseEmitter emitter = new SseEmitter(0L);

        emitters
                .computeIfAbsent(email, e -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> removeEmitter(email, emitter));
        emitter.onTimeout(() -> removeEmitter(email, emitter));
        emitter.onError(e -> removeEmitter(email, emitter));

        return emitter;

    }
    private void removeEmitter(String email, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(email);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) {
                emitters.remove(email);
            }
        }
    }
    @SneakyThrows
    public void sendUser(String email, String message) {
        System.out.println("email++++++: " + email);
        List<SseEmitter> list = emitters.get(email);
        for (SseEmitter emitter : list) {
            try {
                emitter.send(message);
            } catch (Exception e) {
                removeEmitter(email, emitter);
            }
        }
    }
}
