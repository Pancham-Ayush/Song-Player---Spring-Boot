package com.example.Music_Player.Service;

import com.example.Music_Player.Model.User;
import com.example.Music_Player.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        Optional <User> check1 = Optional.ofNullable(userRepo.findByEmail(email));
        if(!check1.isPresent()){
            return null;
        }
        if(check1.get().getPassword().equals(password)){
            return userRepo.findByEmail(email);
        }
        return null;

    }
}
