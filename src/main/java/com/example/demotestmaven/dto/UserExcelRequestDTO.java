package com.example.demotestmaven.dto;

import lombok.Data;

@Data
public class UserExcelRequestDTO {
    String username;
    String password;
    String email;
    String rolecode;
    String roletype;
}
