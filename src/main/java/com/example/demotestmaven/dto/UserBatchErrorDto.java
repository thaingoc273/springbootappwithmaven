package com.example.demotestmaven.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBatchErrorDto {
    private String username;
    private String errorMessage;
    private HttpStatus httpStatus;
}
