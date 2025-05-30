package com.example.demotestmaven.controller;

import com.example.demotestmaven.dto.UserDTO;
import com.example.demotestmaven.dto.UserExcelFullResponseDTO;
import com.example.demotestmaven.service.UserService;
import com.example.demotestmaven.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestHeader("X-Current-User") String currentUsername) {
        logger.info("getAllUsers called with currentUsername: {}", currentUsername);
        return ResponseEntity.ok(userService.getAllUsers(currentUsername));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        logger.info("Get user by username: {}", username);
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

    @PostMapping(value ="/import", consumes = "multipart/form-data")
    public ResponseEntity<List<UserExcelFullResponseDTO>> importUsersFromExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.importUsersFromExcel(file));
    }
} 