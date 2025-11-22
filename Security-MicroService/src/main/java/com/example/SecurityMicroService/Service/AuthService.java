package com.example.SecurityMicroService.Service;

import com.example.SecurityMicroService.Model.User;
import com.example.SecurityMicroService.Repository.UserRepo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JWT_Token jwtToken;
    public User createUser(User user){
        String email = user.getEmail();
        String password = user.getPassword();
        String role = "USER";
        User create_user = userRepo.findByEmail(email);
        if(create_user == null){
            user.setRole(role);
            user.setPassword(passwordEncoder.encode(password));
            userRepo.save(user);
            return user;
        }
        else
            return null;

    }
    public Map<String, String> getEmail_RoleFromCookie(HttpServletRequest request) {
        if(request.getCookies() != null) {
            for(Cookie cookie : request.getCookies()) {
                if(cookie.getName().equals("jwt")) {
                    String token = cookie.getValue();
                    return jwtToken.getEmail_RoleFromToken(token); // your JWT_Token method to extract email ,role
                }
            }
        }
        return null;
    }
    public boolean isMobile(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return false;
        } else {
            String ua = userAgent.toLowerCase();
            return ua.contains("android") || ua.contains("iphone") || ua.contains("ipad") || ua.contains("mobile");
        }
    }
}
