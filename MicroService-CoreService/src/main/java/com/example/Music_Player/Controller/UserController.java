
package com.example.Music_Player.Controller;

import com.example.Music_Player.Model.Admin;
import com.example.Music_Player.Model.User;
import com.example.Music_Player.Repository.AdminRepo;
import com.example.Music_Player.Repository.UserRepo;
import com.example.Music_Player.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    UserRepo userRepo;
    @Autowired
    AdminRepo adminRepo;

    private boolean isMobile(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return false;
        } else {
            String ua = userAgent.toLowerCase();
            return ua.contains("android") || ua.contains("iphone") || ua.contains("ipad") || ua.contains("mobile");
        }
    }

    @PostMapping({"/login"})
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpSession session, HttpServletRequest httpServletRequest) {
        String email = (String)request.get("email");
        String password = (String)request.get("password");
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            log.info("Email or password is empty");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Email or password is empty"));
        }
        User u = this.userService.Login(email, password);
        if (u != null) {
            session.setAttribute("user", u.getName());
            session.setAttribute("useremail", u.getEmail());
            boolean mobile = this.isMobile(httpServletRequest);
            Admin admin = this.adminRepo.getAdminByEmail(u.getEmail());
            return ResponseEntity.ok(Map.of("message", "Login successful", "username", u.getName(), "email", email, "mobile", mobile, "admin", admin != null));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
        }
    }

    @PostMapping({"/createuser"})
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> request, HttpSession session, HttpServletRequest httpServletRequest) {
        User user = new User();
        user.setEmail((String)request.get("email"));
        user.setPassword((String)request.get("password"));
        user.setName((String)request.get("name"));
        User u = this.userService.CreateUser(user);
        if (u != null) {
            session.setAttribute("user", u.getName());
            session.setAttribute("useremail", u.getEmail());
            boolean mobile = this.isMobile(httpServletRequest);
            System.out.println("here 84");
            return ResponseEntity.ok(Map.of("message", "User created successfully", "username", u.getName(), "email", u.getEmail(), "mobile", mobile, "admin", false));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Failed to create user"));
        }
    }

    @PostMapping({"/logout"})
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    @GetMapping({"/current-user"})
    public ResponseEntity<?> currentUser(HttpSession session) {
        String user = (String)session.getAttribute("user");
        String email = (String)session.getAttribute("useremail");
        Admin admin=null ;
        try {
             admin = this.adminRepo.getAdminByEmail(email);
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
        return user != null && email != null ? ResponseEntity.ok(Map.of("username", user, "email", email, "admin", admin != null)) : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not logged in"));
    }

    @GetMapping({"/protected-data"})
    public ResponseEntity<?> protectedData(HttpSession session) {
        String user = (String)session.getAttribute("user");
        return user != null ? ResponseEntity.ok(Map.of("data", "Secret data for " + user)) : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
    }
}
