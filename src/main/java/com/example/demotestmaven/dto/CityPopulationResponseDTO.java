package com.example.demotestmaven.dto;

import java.util.List;

import lombok.Data;

@Data
public class CityPopulationResponseDTO {
    private boolean error;
    private String msg;
    private List<CityDataDTO> data;
    // private boolean scanAvailable;
}