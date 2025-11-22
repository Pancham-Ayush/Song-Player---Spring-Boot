package com.example.SecurityMicroService.Controller;

import com.example.SecurityMicroService.Model.User;
import com.example.SecurityMicroService.Repository.UserRepo;
import com.example.SecurityMicroService.Service.AuthService;
import com.example.SecurityMicroService.Service.JWT_Token;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RestController
public class Google_Login_Controller {

    @Autowired
    private JWT_Token jwtToken;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    AuthService authService;

    @GetMapping("/google")
    public void loginWithGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }
    @GetMapping("/google/me")
    public ResponseEntity<Map> getCurrentUser(HttpServletRequest request) {
        Map<String,String> userInfo= authService.getEmail_RoleFromCookie(request);
        String role = userInfo.get("role");
        String email = userInfo.get("email");
        User user = userRepo.findByEmail(email);
        Map<String,Object> response = Map.of(
                "username", user.getName(),
                "email", user.getEmail(),
                "admin", role.equalsIgnoreCase("ADMIN")
        );
        return ResponseEntity.ok(response);
    }

}
