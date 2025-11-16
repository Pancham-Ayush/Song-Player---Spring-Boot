package com.example.SecurityMicroService.SpringSecurity;

import com.example.SecurityMicroService.Model.User;
import com.example.SecurityMicroService.Repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CoustomUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;

    public CoustomUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        return new CoustomUserDetails(user);
    }
}
