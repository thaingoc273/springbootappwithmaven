package com.example.demotestmaven.repository;

import com.example.demotestmaven.entity.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
  List<Role> findByUser_Username(String username);

  boolean existsByUser_UsernameAndRolecode(String username, String rolecode);
}
