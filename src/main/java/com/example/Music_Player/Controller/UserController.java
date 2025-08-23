package com.example.Music_Player.Controller;

import com.example.Music_Player.Model.Admin;
import com.example.Music_Player.Model.User;
import com.example.Music_Player.Repository.AdminRepo;
import com.example.Music_Player.Repository.UserRepo;
import com.example.Music_Player.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepo userRepo;

    @Autowired
    AdminRepo adminRepo;

    // Helper method to detect mobile
    private boolean isMobile(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            String ua = userAgent.toLowerCase();
            return ua.contains("android") || ua.contains("iphone") || ua.contains("ipad") || ua.contains("mobile");
        }
        return false;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpSession session, HttpServletRequest httpServletRequest) {
        String email = request.get("email");
        String password = request.get("password");


        if (userRepo.existsByEmail(email) && userRepo.findByEmail(email).getPassword().equals(password)) {
            User u = userRepo.findByEmail(email);

            // Store in session
            session.setAttribute("user", u.getName());
            session.setAttribute("useremail", u.getEmail());

            boolean mobile = isMobile(httpServletRequest);
            Admin admin = adminRepo.findAdminByEmail(u.getEmail());

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "username", u.getName(),
                    "email", email,
                    "mobile", mobile,
                    "admin",(admin!=null)
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
    }

    @PostMapping("/createuser")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> request, HttpSession session, HttpServletRequest httpServletRequest) {

        User user1 = userRepo.findByEmail(request.get("email"));

        if (user1== null)
        {
        User user = new User();
        user.setEmail(request.get("email"));
        user.setPassword(request.get("password"));
        user.setName(request.get("name"));

        User u = userService.CreateUser(user);

            session.setAttribute("user", u.getName());
            session.setAttribute("useremail", u.getEmail());
            boolean mobile = isMobile(httpServletRequest);
            System.out.println("here 84");
            return ResponseEntity.ok(Map.of(
                    "message", "User created successfully",
                    "username", u.getName(),
                    "email", u.getEmail(),
                    "mobile", mobile,
                    "admin", false
            ));
        }


        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Failed to create user"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> currentUser(HttpSession session) {
        String user = (String) session.getAttribute("user");
        String email = (String) session.getAttribute("useremail");
        Admin admin = adminRepo.findAdminByEmail(email);
        if (user != null && email != null) {
            return ResponseEntity.ok(Map.of(
                    "username", user,
                    "email", email,
                    "admin", (admin!=null)
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not logged in"));
    }

    @GetMapping("/protected-data")
    public ResponseEntity<?> protectedData(HttpSession session) {
        String user = (String) session.getAttribute("user");
        if (user != null) return ResponseEntity.ok(Map.of("data", "Secret data for " + user));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
    }
}
