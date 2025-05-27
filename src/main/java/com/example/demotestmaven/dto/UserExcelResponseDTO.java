package com.example.demotestmaven.dto;

import java.util.Set;

import lombok.Data;

@Data
public class UserExcelResponseDTO {
    String username;
    String password;
    String email;
    Set<String> roles;
}
