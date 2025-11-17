package com.example.SecurityMicroService.Service;

import com.example.SecurityMicroService.Model.User;
import com.example.SecurityMicroService.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
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
}
