package com.example.demotestmaven.controller;

import com.example.demotestmaven.dto.RoleDTO;
import com.example.demotestmaven.dto.UserDTO;
import com.example.demotestmaven.exception.ValidationException;
import com.example.demotestmaven.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO testUser;
    private List<UserDTO> testUsers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create test user
        testUser = new UserDTO();
        testUser.setId(UUID.randomUUID().toString());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setCreatedAt(LocalDateTime.of(2025, 02, 20, 1, 0, 0));
        testUser.setUpdatedAt(LocalDateTime.of(2025, 02, 20, 0, 0, 0));

        // Create test role
        RoleDTO testRole = new RoleDTO();
        testRole.setId(UUID.randomUUID().toString());
        testRole.setUsername("testuser");
        testRole.setRolecode("testrole");
        testRole.setRoletype("testroletype");
        testRole.setCreatedAt(LocalDateTime.now());
        testRole.setUpdatedAt(LocalDateTime.now());
        testUser.setRoles(Arrays.asList(testRole));

        // Create test users list
        testUsers = Arrays.asList(testUser);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        when(userService.getAllUsers()).thenReturn(testUsers);

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(testUsers, response.getBody());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserByUsername_ShouldReturnUser() {
        // Arrange
        String username = "testuser";
        when(userService.getUserByUsername(username)).thenReturn(testUser);

        // Act
        ResponseEntity<UserDTO> response = userController.getUserByUsername(username);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(testUser, response.getBody());
        verify(userService, times(1)).getUserByUsername(username);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        // Arrange
        String currentUsername = "admin";
        String targetUsername = "testuser";
        UserDTO updatedUser = new UserDTO();
        updatedUser.setUsername(targetUsername);
        updatedUser.setEmail("updated@example.com");
        
        when(userService.updateUser(currentUsername, targetUsername, updatedUser))
            .thenReturn(updatedUser);

        // Act
        ResponseEntity<UserDTO> response = userController.updateUser(
            currentUsername, targetUsername, updatedUser);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(updatedUser, response.getBody());
        verify(userService, times(1))
            .updateUser(currentUsername, targetUsername, updatedUser);
    }

    @Test
    void getUsersByCreatedAtBefore_ShouldReturnListOfUsers() {
        // Arrange
        String dateTimeBefore = "2025-02-20";
        when(userService.getUsersByCreatedAtBefore(dateTimeBefore)).thenReturn(testUsers);

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getUsersByCreatedAtBefore(dateTimeBefore);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(testUsers, response.getBody());
        verify(userService, times(1)).getUsersByCreatedAtBefore(dateTimeBefore);
    }

        
    @Test
    void getUsersByCreatedAtBefore_ShouldReturnNoUsersFound() {
        // Arrange
        String dateTimeBefore = "2025-02-20";
        when(userService.getUsersByCreatedAtBefore(dateTimeBefore)).thenReturn(Collections.emptyList());
        
        // Act
        ResponseEntity<List<UserDTO>> response = userController.getUsersByCreatedAtBefore(dateTimeBefore);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }   
    // Test for Bad request
} 