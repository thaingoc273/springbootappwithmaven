package com.example.demotestmaven.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBatchSuccessfulDto {
    private String username;
    private String email;
    private String password;
}
