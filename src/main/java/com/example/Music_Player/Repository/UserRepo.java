package com.example.Music_Player.Repository;

import com.example.Music_Player.Model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends CrudRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email);
}
