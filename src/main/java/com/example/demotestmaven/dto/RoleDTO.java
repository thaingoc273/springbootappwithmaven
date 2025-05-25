package com.example.demotestmaven.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoleDTO {
    private String id;
    private String username;
    private String rolecode;
    private String roletype;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAtLocal;
} 