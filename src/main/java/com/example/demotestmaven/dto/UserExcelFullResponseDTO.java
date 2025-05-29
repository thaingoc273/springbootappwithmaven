package com.example.demotestmaven.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserExcelFullResponseDTO {
    String username;
    String status;
    List<String> messages;
    
    @Override
    public String toString() {
        return "Username= " + username + ", status= " + status + ", message= " + messages;
    }
}