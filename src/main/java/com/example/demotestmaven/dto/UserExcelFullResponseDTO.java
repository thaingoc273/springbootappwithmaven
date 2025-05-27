package com.example.demotestmaven.dto;


import lombok.Data;

@Data
public class UserExcelFullResponseDTO {
    String username;
    String message;

    @Override
    public String toString() {
        return "username = " + username + ", message=" + message;
    }
}