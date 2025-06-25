package com.example.demotestmaven.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserExcelFullResponseDTO {
  private String rowNumber;
  private String username;
  private String statusUpdate;
  private List<String> messages;

  @Override
  public String toString() {
    return "Username= " + username + ", status= " + statusUpdate + ", message= " + messages;
  }
}
