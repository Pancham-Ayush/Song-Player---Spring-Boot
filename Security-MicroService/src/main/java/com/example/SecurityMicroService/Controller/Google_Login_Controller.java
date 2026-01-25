package com.example.SecurityMicroService.Controller;

import com.example.SecurityMicroService.Model.User;
import com.example.SecurityMicroService.Repository.UserRepo;
import com.example.SecurityMicroService.Service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class Google_Login_Controller {

    private final UserRepo userRepo;

    private final AuthService authService;

    public Google_Login_Controller(UserRepo userRepo, AuthService authService) {
        this.userRepo = userRepo;
        this.authService = authService;
    }

    @GetMapping("/google")
    public void loginWithGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/google/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpServletRequest request) {
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
