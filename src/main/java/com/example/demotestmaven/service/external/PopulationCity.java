package com.example.demotestmaven.service.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demotestmaven.dto.CityPopulationResponseDTO;
import com.google.gson.Gson;

import jakarta.transaction.Transactional;

@Service
public class PopulationCity {

    private static final Logger logger = LoggerFactory.getLogger(PopulationCity.class);

    @Transactional
    public CityPopulationResponseDTO callExternalAPI_getPopulationByCities() {       
        String url = "https://countriesnow.space/api/v0.1/countries/population/cities";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        // logger.info("response: {}", response);
        Gson gson = new Gson();
        return gson.fromJson(response, CityPopulationResponseDTO.class);
    }
} 