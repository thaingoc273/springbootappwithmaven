package com.example.demotestmaven.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demotestmaven.dto.CityPopulationResponseDTO;
import com.example.demotestmaven.service.external.CityPopulationService;
import com.fasterxml.jackson.core.JsonProcessingException;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/external")
public class ExternalController {
    
    @Autowired
    private CityPopulationService cityPopulationService;

    @GetMapping("/population/cities")
    public ResponseEntity<?> callExternalAPI_getPopulationByCities() {
        try {
            return ResponseEntity.ok(cityPopulationService.callExternalAPI_getPopulationByCitiesUsingObjectMapper());
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().body("Error processing JSON response: " + e.getMessage());
        }
    }

    @GetMapping("/webclient/population/cities")
    public Mono<ResponseEntity<CityPopulationResponseDTO>> callExternalAPI_getPopulationByCitiesUsingWebClient() {
        return cityPopulationService.callExternalAPI_getPopulationByCitiesUsingWebClient()
            .map(ResponseEntity::ok)
            .onErrorResume(e -> {
                CityPopulationResponseDTO errorResponse = new CityPopulationResponseDTO();
                errorResponse.setError(true);
                errorResponse.setMsg(e.getMessage());
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
            });
    }

    @GetMapping("/webclient/population/cities/version2")
    public Mono<ResponseEntity<CityPopulationResponseDTO>> callExternalAPI_getPopulationByCitiesUsingWebClient_Version2() {
        return cityPopulationService.callExternalAPI_getPopulationByCitiesUsingWebClient_Version2()
            .map(ResponseEntity::ok)
            .onErrorResume(e -> {
                CityPopulationResponseDTO errorResponse = new CityPopulationResponseDTO();
                errorResponse.setError(true);
                errorResponse.setMsg(e.getMessage());
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
            });
    }
}
