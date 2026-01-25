package com.example.SecurityMicroService.Controller;

import com.example.SecurityMicroService.Model.User;
import com.example.SecurityMicroService.Repository.UserRepo;
import com.example.SecurityMicroService.Service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController

public class VerifyController {

    private final UserRepo userRepo;

    private final AuthService authService;

    public VerifyController(UserRepo userRepo, AuthService authService) {
        this.userRepo = userRepo;
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication, HttpServletRequest request) {
        if (authentication == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Map<String, String> userInfo = authService.getEmail_RoleFromCookie(request);
        if (userInfo == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String role = userInfo.get("role");
        String email = userInfo.get("email");
        User user = userRepo.findByEmail(email);
        Map<String, Object> response = Map.of(
                "username", user.getName(),
                "email", user.getEmail(),
                "admin", role.equalsIgnoreCase("ADMIN")
        );
        return ResponseEntity.ok(response);
    }

}
