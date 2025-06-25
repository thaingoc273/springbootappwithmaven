package com.example.demotestmaven.dto;

import java.util.List;
import lombok.Data;

@Data
public class UserResponseBatch {
  private int successCount;
  private int failureCount;
  private float successRate;
  private List<UserItemResponseDto> results;
}
