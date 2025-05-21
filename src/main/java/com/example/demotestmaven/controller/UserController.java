package com.example.demotestmaven.controller;

import com.example.demotestmaven.dto.UserDTO;
import com.example.demotestmaven.service.UserService;
import com.example.demotestmaven.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserDTO> updateUser(
            @RequestHeader("X-Current-User") String currentUsername,
            @PathVariable String username,
            @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(currentUsername, username, userDTO));
    }

    @GetMapping("/date/{dateTimeBefore}")
    public ResponseEntity<List<UserDTO>> getUsersByCreatedAtBefore(@PathVariable String dateTimeBefore) {
        return ResponseEntity.ok(userService.getUsersByCreatedAtBefore(dateTimeBefore));
    }
} 