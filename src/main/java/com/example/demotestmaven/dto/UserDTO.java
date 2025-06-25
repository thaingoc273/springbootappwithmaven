package com.example.demotestmaven.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class UserDTO {
  private String id;
  private String username;
  private String email;
  private String password;
  private Set<String> roletypes;
  private List<RoleDTO> roles;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime createdAtLocal;
}
