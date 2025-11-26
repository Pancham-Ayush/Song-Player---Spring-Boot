package com.example.SecurityMicroService.Service;

import com.example.SecurityMicroService.Model.User;
import com.example.SecurityMicroService.Repository.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JWT_Token jwtToken;
    private final UserRepo userRepo;
    private final AuthService authService;
    private final Executor virtualThreadExecutor;

    public CustomOAuth2SuccessHandler(JWT_Token jwtToken, UserRepo userRepo, AuthService authService, Executor virtualThreadExecutor) {
        this.jwtToken = jwtToken;
        this.userRepo = userRepo;
        this.authService = authService;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    @SneakyThrows
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {

        Future<?> future = ((ExecutorService) virtualThreadExecutor)
                .submit(() ->
                {
                    try {
                        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
                        String email = oauthUser.getAttribute("email");
                        String name = oauthUser.getAttribute("name");

                        User user = userRepo.findByEmail(email);
                        if (user == null) {
                            user = new User();
                            user.setEmail(email);
                            user.setName(name);
                            user.setRole("USER");
                            user.setPassword(UUID.randomUUID().toString());
                            authService.createUser(user);
                        }
                        String token = jwtToken.getSecretToken(email, user.getRole());
                        Cookie cookie = new Cookie("jwt", token);
                        cookie.setHttpOnly(true);
                        cookie.setPath("/");
                        cookie.setMaxAge(7 * 24 * 60 * 60);
                        response.addCookie(cookie);
                        boolean device = authService.isMobile(request);
                        boolean adminRole = false;
                        if (user.getRole().equalsIgnoreCase("ADMIN"))
                            adminRole = true;
                        Map<String, Object> jsonMap = Map.of(
                                "message", "Login successful",
                                "username", name,
                                "email", email,
                                "mobile", device,
                                "admin", adminRole
                        );
                        String json = new ObjectMapper().writeValueAsString(jsonMap);
                        response.setContentType("text/html");
                        response.getWriter().write(
                                "<html><body>" +
                                        "<script>" +
                                        "console.log('Login successful');" +
                                        "window.close();" +  // close the popup tab
                                        "</script>" +
                                        "</body></html>"
                        );
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });
       future.get();
    }
}
