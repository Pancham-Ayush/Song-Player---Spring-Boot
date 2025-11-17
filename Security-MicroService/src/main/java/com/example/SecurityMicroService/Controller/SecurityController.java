package com.example.SecurityMicroService.Controller;

import com.example.SecurityMicroService.Model.User;
import com.example.SecurityMicroService.Service.AuthService;
import com.example.SecurityMicroService.Service.JWT_Token;
import com.example.SecurityMicroService.SpringSecurity.CoustomUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class SecurityController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JWT_Token jwtToken;

    @Autowired
    AuthService authService;


    @PostMapping("/manual-create-user")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        log.info("Creating user {}", user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        User create_user = authService.createUser(user);
        if (create_user != null) {
            return ResponseEntity.ok("Created Successfully");
        }
        return ResponseEntity.notFound().build();
    }

    private boolean isMobile(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return false;
        } else {
            String ua = userAgent.toLowerCase();
            return ua.contains("android") || ua.contains("iphone") || ua.contains("ipad") || ua.contains("mobile");
        }
    }

    @PostMapping("/manual-login")
    public ResponseEntity<?> manualLogin(@RequestBody Map<String, String> request, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
        String email = (String) request.get("email");
        String password = (String) request.get("password");
        Authentication authentication = null;

        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password"));
        }
        String token = jwtToken.getSecretToken(email);
        Map<String, Object> response = new HashMap<>();
        CoustomUserDetails userDetails = (CoustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        boolean admin_role = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        httpServletResponse.addCookie(cookie);
        Map<String, Object> body = new HashMap<>();
        boolean device = isMobile(httpServletRequest);
        body.put("message", "Login successful");
        body.put("email", email);

        return ResponseEntity.ok(Map.of("message", "Login successful", "username", username, "email", email, "mobile", device, "admin", admin_role));

    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().invalidate();
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        httpServletResponse.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

}
