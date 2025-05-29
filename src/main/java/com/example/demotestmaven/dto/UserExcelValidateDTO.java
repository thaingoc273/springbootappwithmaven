package com.example.demotestmaven.dto;

import java.util.HashMap;

import com.example.demotestmaven.entity.User;

import lombok.Data;

@Data
public class UserExcelValidateDTO {
    private UserExcelFullResponseDTO userExcelFullResponseDTO;
    private HashMap<String, User> userHashMap;
}
