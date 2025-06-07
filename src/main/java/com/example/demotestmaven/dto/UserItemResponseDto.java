package com.example.demotestmaven.dto;

import lombok.Data;

@Data
public class UserItemResponseDto {
    private String username;
    private String status;
    private String message;
    private String errorMessage;
}
