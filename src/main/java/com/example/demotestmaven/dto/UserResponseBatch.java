package com.example.demotestmaven.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserResponseBatch {
    private int successCount;
    private int failureCount;
    private float successRate;
    private List<UserItemResponseDto> results;
}