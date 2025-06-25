package com.example.demotestmaven.dto;

import com.example.demotestmaven.entity.User;
import java.util.HashMap;
import lombok.Data;

@Data
public class UserExcelValidateDTO {
  private UserExcelFullResponseDTO userExcelFullResponseDTO;
  private HashMap<String, User> userHashMap;
}
