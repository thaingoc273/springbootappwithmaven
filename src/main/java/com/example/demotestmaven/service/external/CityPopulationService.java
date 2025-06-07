package com.example.demotestmaven.service.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demotestmaven.dto.CityPopulationResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
public class CityPopulationService {

    private static final Logger logger = LoggerFactory.getLogger(CityPopulationService.class);

    @Autowired
    private WebClient.Builder webClientBuilder;


    @Transactional
    public CityPopulationResponseDTO callExternalAPI_getPopulationByCities() {       
        String url = "https://countriesnow.space/api/v0.1/countries/population/cities";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        // logger.info("response: {}", response);
        Gson gson = new Gson();
        return gson.fromJson(response, CityPopulationResponseDTO.class);
    }

    // Using ObjectMapper to parse the JSON response and return the data as a JsonNode
    @Transactional
    public JsonNode callExternalAPI_getPopulationByCitiesUsingObjectMapper() throws JsonProcessingException {       
        String url = "https://countriesnow.space/api/v0.1/countries/population/cities";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(response);
        JsonNode data = root.path("data");
        return data;
    }

    @Transactional
    public Mono<CityPopulationResponseDTO> callExternalAPI_getPopulationByCitiesUsingWebClient() {
        return webClientBuilder.build()
            .get()
            .uri("/countries/population/cities")
            .retrieve()
            .bodyToMono(String.class)
            .doOnNext(response -> logger.info("Raw API Response: {}", response))
            .flatMap(response -> {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    // JsonNode rootNode = objectMapper.readTree(response);
                    // logger.info("Response structure: {}", rootNode.toPrettyString());
                    
                    CityPopulationResponseDTO dto = objectMapper.readValue(response, CityPopulationResponseDTO.class);
                    // logger.info("Parsed DTO: {}", dto);
                    
                    if (dto.isError()) {
                        logger.error("API returned error: {}", dto.getMsg());
                        return Mono.error(new RuntimeException("API Error: " + dto.getMsg()));
                    }
                    
                    return Mono.just(dto);
                } catch (JsonProcessingException e) {
                    logger.error("Error parsing response: {}", e.getMessage());
                    return Mono.error(e);
                }
            })
            .onErrorResume(e -> {
                logger.error("Error calling external API: {}", e.getMessage());
                return Mono.error(e);
            });
    }
    
    @Transactional
    public Mono<CityPopulationResponseDTO> callExternalAPI_getPopulationByCitiesUsingWebClient_Version2() {
        return webClientBuilder.build()
            .get()
            .uri("/countries/population/cities")
            .retrieve()
            .bodyToMono(CityPopulationResponseDTO.class)
            .doOnNext(dto -> logger.info("Parsed DTO: {}", dto))
            .onErrorResume(e -> {
                logger.error("Error calling external API: {}", e.getMessage());
                return Mono.error(e);
            });
    }

}
