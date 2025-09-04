package com.example.Music_Player.Service;

import com.example.Music_Player.Model.User;
import com.example.Music_Player.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;
    public User CreateUser(User user){
        if(userRepo.findByEmail(user.getEmail())!=null){
            return null;
        }
        userRepo.save(user);
        return user;
    }
    public User Login(String email, String password){
        User user = userRepo.findByEmail(email);
        if(user == null){
            return null;
        }
        if(user.getPassword().equals(password)){
            return user;
        }
        return null;

    }
}
