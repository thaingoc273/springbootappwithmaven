package com.example.demotestmaven.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserExcelRequestDTO {
    private String rowNumber;
    private String username;
    private String password;
    private String email;
    private String rolecodes;
    private String roletypes;
}
