package com.example.demotestmaven.dto;

import java.util.List;
import lombok.Data;

@Data
public class CityDataDTO {
  private String city;
  private String country;
  private List<PopulationCountDTO> populationCounts;
}
