package com.example.demotestmaven.controller;

import com.example.demotestmaven.dto.RoleDTO;
import com.example.demotestmaven.dto.UserDTO;
import com.example.demotestmaven.dto.UserExcelFullResponseDTO;
import com.example.demotestmaven.exception.ValidationException;
import com.example.demotestmaven.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO testUser;
    private List<UserDTO> testUsers;

    private String currentUsername = "testuser";
    private String dateTimeBefore = "2025-02-30";
    private String dateTimeAfter = "2025-02-20";
    private String adminUser = "testuser1";
    private String uploadType = "file";

    private String testDataPath = "testdata/";

    private String testNormalFile = testDataPath + "test_normal.xlsx";
    private String testEmptyFile = testDataPath + "test_empty.xlsx";
    private String testDuplicateUsernameFile = testDataPath + "test_duplicateusername.xlsx";
    private String testDuplicateEmailFile = testDataPath + "test_duplicateemail.xlsx";

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
        testRole.setRolecode("ADMIN");
        testRole.setRoletype("SYSTEM_ADMIN");
        testRole.setCreatedAt(LocalDateTime.now());
        testRole.setUpdatedAt(LocalDateTime.now());
        testUser.setRoles(Arrays.asList(testRole));

        // Create test users list
        testUsers = Arrays.asList(testUser);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        when(userService.getAllUsers(adminUser)).thenReturn(testUsers);

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getAllUsers(adminUser);

        // Assert
        assertNotNull(response);        
        assertEquals(200, response.getStatusCode().value());
        assertEquals(testUsers, response.getBody());
        verify(userService, times(1)).getAllUsers(adminUser);
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
    void getUsersByCreatedAtBeforeAndAfter_WhenValidateTimeBeforeAndAfter_ShouldReturnListOfUsers() {
        // Arrange
        when(userService.getUsersByCreatedAtBeforeAndAfter(currentUsername, dateTimeBefore, dateTimeAfter)).thenReturn(testUsers);

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getUsersByCreatedAtBeforeAndAfter(currentUsername, dateTimeBefore, dateTimeAfter);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(testUsers, response.getBody());
        verify(userService, times(1)).getUsersByCreatedAtBeforeAndAfter(currentUsername, dateTimeBefore, dateTimeAfter);
    }       
 
    @Test
    void getUsersByCreatedAtBeforeAndAfter_WhenInvalidateTimeBeforeAndAfter_ShouldThrowValidationException() {
        // Arrange

        when(userService.getUsersByCreatedAtBeforeAndAfter(currentUsername, dateTimeBefore, dateTimeAfter))
            .thenThrow(new ValidationException("Invalid date time format, please use yyyy-MM-dd"));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> userController.getUsersByCreatedAtBeforeAndAfter(currentUsername, dateTimeBefore, dateTimeAfter));
        
        assertEquals("Invalid date time format, please use yyyy-MM-dd", exception.getMessage());
        verify(userService, times(1)).getUsersByCreatedAtBeforeAndAfter(currentUsername, dateTimeBefore, dateTimeAfter);
    }


    private MultipartFile getMockMultipartFile(String fileName) {
        try {
            ClassPathResource resource = new ClassPathResource(fileName);
            InputStream fileInputStream = resource.getInputStream();
            String filename = resource.getFilename();
            MockMultipartFile multipartFile = new MockMultipartFile(
                                                             uploadType, 
                                                             filename, 
                                                             MediaType.MULTIPART_FORM_DATA_VALUE, 
                                                             fileInputStream);
            return multipartFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to get mock multipart file", e);
        }
    }
}