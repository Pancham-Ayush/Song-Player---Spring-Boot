package com.example.Music_Player.Repository;

import com.example.Music_Player.Model.Admin;
import org.springframework.data.repository.CrudRepository;

public interface AdminRepo extends CrudRepository<Admin, Long> {
    public Admin findAdminByEmail(String email);
}
