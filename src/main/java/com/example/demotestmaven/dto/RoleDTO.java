package com.example.demotestmaven.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
public class RoleDTO {
    private String id;
    private String username;
    private String rolecode;
    private String roletype;
    private LocalDateTime createdAt;
    private LocalDateTime createdAtZone;
    private LocalDateTime updatedAt;
} 