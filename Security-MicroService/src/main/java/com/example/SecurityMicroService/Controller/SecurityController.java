package com.example.SecurityMicroService.Controller;

import com.example.SecurityMicroService.Model.User;
import com.example.SecurityMicroService.Service.AuthService;
import com.example.SecurityMicroService.Service.JWT_Token;
import com.example.SecurityMicroService.SpringSecurity.CoustomUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
@RestController
public class SecurityController {

    private final AuthenticationManager authenticationManager;

    private final JWT_Token jwtToken;

    private final AuthService authService;

    private final Executor virtualThreadExecutor;

    public SecurityController(AuthenticationManager authenticationManager, JWT_Token jwtToken, AuthService authService, Executor virtualThreadExecutor) {
        this.authenticationManager = authenticationManager;
        this.jwtToken = jwtToken;
        this.authService = authService;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    @PostMapping("/manual-create-user")
    public ResponseEntity<String> createUser(@RequestBody User user) {

        user.setRole("USER");
        User create_user = authService.createUser(user);
        if (create_user != null) {
            return ResponseEntity.ok("Created Successfully");
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/manual-login")
    public ResponseEntity<Object> manualLogin(@RequestBody Map<String, String> request, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws ExecutionException, InterruptedException {
        Future<ResponseEntity<Object>> future = ((ExecutorService) virtualThreadExecutor)
                .submit(() -> {
                    String email = (String) request.get("email");

                    String password = (String) request.get("password");
                    Authentication authentication = null;
                    try {
                        authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password"));
                    }
                    CoustomUserDetails userDetails = (CoustomUserDetails) authentication.getPrincipal();
                    String username = userDetails.getUsername();
                    String role = userDetails.getAuthorities().iterator().next().getAuthority();
                    String token = jwtToken.getSecretToken(email, role);
                    boolean admin_role = role.equals("ADMIN");
                    Cookie cookie = new Cookie("jwt", token);
                    cookie.setHttpOnly(true);
                    cookie.setSecure(true);
                    cookie.setPath("/");
                    cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
                    httpServletResponse.addCookie(cookie);
                    Map<String, Object> body = new HashMap<>();
                    boolean device = authService.isMobile(httpServletRequest);
                    body.put("message", "Login successful");
                    body.put("email", email);

                    return ResponseEntity.ok(Map.of("message", "Login successful", "username", username, "email", email, "mobile", device, "admin", admin_role));
                });
        return future.get();
    }

}
