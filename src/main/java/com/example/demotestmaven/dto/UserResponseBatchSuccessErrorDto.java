package com.example.demotestmaven.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseBatchSuccessErrorDto {
    private int successCount;
    private int failureCount;
    private float successRate;   
    List<UserBatchSuccessfulDto> successfulUsers;
    List<UserBatchErrorDto> errorUsers;
}
