
package com.example.Music_Player.Controller;

import com.example.Music_Player.Model.Admin;
import com.example.Music_Player.Repository.AdminRepo;
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


}
