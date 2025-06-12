package com.example.demotestmaven.controller;

import com.example.demotestmaven.dto.UserDTO;
import com.example.demotestmaven.dto.UserExcelFullResponseDTO;
import com.example.demotestmaven.dto.UserRequestDTO;
import com.example.demotestmaven.dto.UserResponseDTO;
import com.example.demotestmaven.dto.UserResponseBatch;
import com.example.demotestmaven.dto.UserResponseBatchSuccessErrorDto;
import com.example.demotestmaven.service.UserService;
import com.example.demotestmaven.service.external.CityPopulationService;
import com.example.demotestmaven.constants.GlobalConstants;
import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.example.demotestmaven.exception.ApiException;
import com.example.demotestmaven.exception.ApiErrorType;
import com.example.demotestmaven.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CityPopulationService cityPopulationService;


    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestHeader("X-Current-User") String currentUsername) {
        log.info("getAllUsers called with currentUsername: {}", currentUsername);
        return ResponseEntity.ok(userService.getAllUsers(currentUsername));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        log.info("Get user by username: {}", username);
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserDTO> updateUser(
            @RequestHeader("X-Current-User") String currentUsername,
            @PathVariable String username,
            @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(currentUsername, username, userDTO));
    }

    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getUsersByCreatedAtBeforeAndAfter(        
        @RequestHeader("X-Current-User") String currentUsername,
        @RequestParam(required = false, defaultValue = "2099-01-01", value = "before") String beforeDate,
        @RequestParam(required = false, defaultValue = "1999-01-01", value = "after") String afterDate
        ) {
        return ResponseEntity.ok(userService.getUsersByCreatedAtBeforeAndAfter(currentUsername,beforeDate, afterDate));
    }

    @PostMapping("/")
    public ResponseEntity<UserDTO> createUser(@RequestHeader("X-Current-User") String currentUsername, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(currentUsername, userDTO));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<UserResponseDTO>> createUsers(
            @RequestHeader("X-Current-User") String currentUsername,
            @RequestBody List<UserRequestDTO> userRequestDTOs) {
        log.info("Creating batch of {} users", userRequestDTOs.size());
        return ResponseEntity.ok(userService.createUsers(currentUsername, userRequestDTOs));
    }
    @PostMapping("/batch_async")
    public Mono<ResponseEntity<UserResponseBatch>> createUsersBatchAsync(
            @RequestHeader("X-Current-User") String currentUsername,
            @RequestBody List<UserRequestDTO> userRequestDTOs) {       
        return userService.createUsersBatchAsync(currentUsername, userRequestDTOs)
        .map(result -> {
            if (result.getSuccessRate() >= 80) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        }
        );
    }
    @PostMapping(value ="/import", consumes = "multipart/form-data")
    public ResponseEntity<List<UserExcelFullResponseDTO>> importUsersFromExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.importUsersFromExcel(file));
    }

    @GetMapping("/population/cities")
    public ResponseEntity<?> callExternalAPI_getPopulationByCities() {
        return ResponseEntity.ok(cityPopulationService.callExternalAPI_getPopulationByCities());     
    }

    @GetMapping("/permission/{username}")
    public ResponseEntity<?> getPermissionByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getPermissionByUsername(username));
    }

    @PostMapping("/batch_success_error")
    public ResponseEntity<UserResponseBatchSuccessErrorDto> createUsersBatchSuccessError(
            @RequestHeader("X-Current-User") String currentUsername,
            @RequestBody List<UserRequestDTO> userRequestDTOs) {
        try {
            UserResponseBatchSuccessErrorDto result = userService.createUsersBatchSuccessError(currentUsername, userRequestDTOs);
            if (result.getSuccessRate() >= 80) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        } catch (Exception e) {
            // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            throw new ApiException(ApiErrorType.DATABASE_ERROR, "An unexpected database error occurred");
        }
    }

} 