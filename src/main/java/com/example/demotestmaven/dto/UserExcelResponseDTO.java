package com.example.demotestmaven.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserExcelResponseDTO {
    String username;
    String password;
    String email;
    Set<String> roles;
}
