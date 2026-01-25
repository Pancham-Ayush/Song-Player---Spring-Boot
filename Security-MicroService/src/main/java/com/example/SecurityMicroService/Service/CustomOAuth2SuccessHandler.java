package com.example.SecurityMicroService.Service;

import com.example.SecurityMicroService.Model.User;
import com.example.SecurityMicroService.Repository.UserRepo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JWT_Token jwtToken;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2SuccessHandler(JWT_Token jwtToken, UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.jwtToken = jwtToken;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        User user = userRepo.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setRole("USER");
            // For OAuth users we generate a random password and encode it
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            userRepo.save(user);
        }
        String token = jwtToken.getSecretToken(email, user.getRole());
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);
        response.setContentType("text/html");
        response.getWriter().write(
                "<html><body>" +
                        "<script>" +
                        "console.log('Login successful');" +
                        "window.close();" +
                        "</script>" +
                        "</body></html>"
        );
    }
}
