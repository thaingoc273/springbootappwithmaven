package com.example.demotestmaven.service;

import com.example.demotestmaven.dto.RoleDTO;
import com.example.demotestmaven.entity.Role;
import com.example.demotestmaven.entity.User;
import com.example.demotestmaven.repository.RoleRepository;
import com.example.demotestmaven.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {

  @Autowired private RoleRepository roleRepository;

  @Autowired private UserRepository userRepository;

  public List<RoleDTO> getAllRoles() {
    return roleRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  public List<RoleDTO> getRolesByUsername(String username) {
    return roleRepository.findByUser_Username(username).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Transactional
  public RoleDTO assignRole(String currentUsername, String targetUsername, RoleDTO roleDTO) {
    User currentUser =
        userRepository
            .findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("Current user not found"));

    User targetUser =
        userRepository
            .findByUsername(targetUsername)
            .orElseThrow(() -> new RuntimeException("Target user not found"));

    // Check if current user has permission to assign roles
    if (!canAssignRole(currentUser)) {
      throw new RuntimeException("You don't have permission to assign roles");
    }

    // Check if role already exists
    if (roleRepository.existsByUser_UsernameAndRolecode(targetUsername, roleDTO.getRolecode())) {
      throw new RuntimeException("Role already assigned to user");
    }

    Role role = new Role();
    role.setUser(targetUser);
    role.setRolecode(roleDTO.getRolecode());
    role.setRoletype(roleDTO.getRoletype());

    return convertToDTO(roleRepository.save(role));
  }

  @Transactional
  public void removeRole(String currentUsername, String targetUsername, String rolecode) {
    User currentUser =
        userRepository
            .findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("Current user not found"));

    // Check if current user has permission to remove roles
    if (!canAssignRole(currentUser)) {
      throw new RuntimeException("You don't have permission to remove roles");
    }

    roleRepository.findByUser_Username(targetUsername).stream()
        .filter(role -> role.getRolecode().equals(rolecode))
        .findFirst()
        .ifPresent(role -> roleRepository.delete(role));
  }

  private boolean canAssignRole(User user) {
    return roleRepository.findByUser_Username(user.getUsername()).stream()
        .anyMatch(
            role -> role.getRolecode().equals("ADMIN") || role.getRolecode().equals("MANAGER"));
  }

  private RoleDTO convertToDTO(Role role) {
    RoleDTO dto = new RoleDTO();
    dto.setId(role.getId());
    dto.setUsername(role.getUser().getUsername());
    dto.setRolecode(role.getRolecode());
    dto.setRoletype(role.getRoletype());
    return dto;
  }
}
