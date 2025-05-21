package com.example.demotestmaven.repository;

import com.example.demotestmaven.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoleRepository extends JpaRepository<Role, String> {
    List<Role> findByUser_Username(String username);
    boolean existsByUser_UsernameAndRolecode(String username, String rolecode);
} 