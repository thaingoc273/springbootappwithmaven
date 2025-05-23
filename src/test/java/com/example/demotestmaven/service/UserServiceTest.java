package com.example.demotestmaven.service;

import com.example.demotestmaven.dto.UserDTO;
import com.example.demotestmaven.entity.User;
import com.example.demotestmaven.exception.UnauthorizedException;
import com.example.demotestmaven.exception.ValidationException;
import com.example.demotestmaven.entity.Role;
import com.example.demotestmaven.repository.UserRepository;
import com.example.demotestmaven.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demotestmaven.exception.BusinessException;
import com.example.demotestmaven.exception.ErrorCode;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;
    private Role adminRole;
    private Role managerRole;
    private User adminUser;
    
    private String adminUsername = "ngoc";
    private String normalUsername = "phuong";
    private String dateBefore = "2025-05-30";
    private String dateAfter = "2025-05-15";
    private String invalidDate = "invalid-date-format";    

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create test user
        testUser = new User();
        testUser.setId(UUID.randomUUID().toString());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        LocalDateTime now = LocalDateTime.of(2025, 02, 20, 1, 0, 0);
        testUser.setCreatedAt(now);
        testUser.setCreatedAtZone(now.atZone(ZoneId.systemDefault()));
        testUser.setUpdatedAt(now);

        // Create test user DTO
        testUserDTO = new UserDTO();
        testUserDTO.setId(testUser.getId());
        testUserDTO.setUsername(testUser.getUsername());
        testUserDTO.setEmail(testUser.getEmail());
        testUserDTO.setPassword(testUser.getPassword());
        testUserDTO.setCreatedAt(testUser.getCreatedAt());
        testUserDTO.setCreatedAtZone(testUser.getCreatedAtZone());
        testUserDTO.setUpdatedAt(testUser.getUpdatedAt());

        // Create admin user
        adminUser = new User();  // Initialize the adminUser object
        adminUser.setId(UUID.randomUUID().toString());
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("encodedPassword");
        adminUser.setCreatedAt(now);
        adminUser.setCreatedAtZone(now.atZone(ZoneId.systemDefault()));
        adminUser.setUpdatedAt(now);

        // Create test roles
        adminRole = new Role();
        adminRole.setId(UUID.randomUUID().toString());
        adminRole.setUser(adminUser);
        adminRole.setRolecode("ADMIN");
        adminRole.setRoletype("SYSTEM_ADMIN");
        adminRole.setCreatedAt(LocalDateTime.of(2025, 02, 20, 1, 0, 0));
        adminRole.setUpdatedAt(LocalDateTime.of(2025, 02, 20, 1, 0, 0));

        managerRole = new Role();
        managerRole.setId(UUID.randomUUID().toString());
        managerRole.setUser(adminUser);
        managerRole.setRolecode("MANAGER");
        managerRole.setRoletype("TEAM_MANAGER");
        managerRole.setCreatedAt(LocalDateTime.of(2025, 02, 20, 1, 0, 0));
        managerRole.setUpdatedAt(LocalDateTime.of(2025, 02, 20, 1, 0, 0));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        when(userRepository.findAllWithRoles()).thenReturn(Arrays.asList(testUser));

        // Act
        List<UserDTO> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
        verify(userRepository, times(1)).findAllWithRoles();
    }

    @Test
    void getUserByUsername_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDTO result = userService.getUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void getUserByUsername_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.getUserByUsername("nonexistent"));
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void updateUser_WhenUserEditsOwnProfile_ShouldUpdateSuccessfully() {
        // Arrange
        String username = "testuser";
        UserDTO updateDTO = new UserDTO();
        updateDTO.setUsername(username);
        updateDTO.setEmail("updated@example.com");
        updateDTO.setPassword("newPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        // when(passwordEncoder.encode(any())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDTO result = userService.updateUser(username, username, updateDTO);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(2)).findByUsername(username);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WhenAdminEditsUser_ShouldUpdateSuccessfully() {
        // Arrange
        User adminUser = new User();
        adminUser.setUsername("admin");
        
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(roleRepository.findByUser_Username("admin")).thenReturn(Arrays.asList(adminRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDTO updateDTO = new UserDTO();
        updateDTO.setUsername("testuser");
        updateDTO.setEmail("updated@example.com");

        // Act
        UserDTO result = userService.updateUser("admin", "testuser", updateDTO);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findByUsername("admin");
        verify(userRepository, times(1)).findByUsername("testuser");
        //verify(roleRepository, times(1)).findByUser_Username("admin");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserWithoutPermissionTriesToEdit_ShouldThrowException() {
        // Arrange
        String currentUsername = "user1";
        String targetUsername = "user2";
        UserDTO updateDTO = new UserDTO();
        updateDTO.setUsername(targetUsername);
        
        when(userRepository.findByUsername(currentUsername))
                .thenReturn(Optional.of(createUser(currentUsername)));
        when(userRepository.findByUsername(targetUsername))
                .thenReturn(Optional.of(createUser(targetUsername)));
        when(roleRepository.findByUser_Username(currentUsername))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updateUser(currentUsername, targetUsername, updateDTO));
        assertEquals(ErrorCode.FORBIDDEN_OPERATION.getMessage(), exception.getMessage());
    }

    @Test
    void getUsersByCreatedAtBefore_WhenNormal_ShouldReturnListOfUsers() {
        // Arrange
        when(roleRepository.findByUser_Username(adminUsername)).thenReturn(Arrays.asList(adminRole));
        when(userRepository.findByCreatedAtBeforeAndAfter(convertToLocalDateTime(dateBefore), convertToLocalDateTime(dateAfter))).thenReturn(Arrays.asList(testUser));

        // Act
        List<UserDTO> result = userService.getUsersByCreatedAtBeforeAndAfter(adminUsername, dateBefore, dateAfter);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
        assertEquals(testUser.getEmail(), result.get(0).getEmail());
        assertEquals(testUser.getCreatedAt(), result.get(0).getCreatedAt());
        assertEquals(testUser.getUpdatedAt(), result.get(0).getUpdatedAt());
        verify(userRepository, times(1)).findByCreatedAtBeforeAndAfter(convertToLocalDateTime(dateBefore), convertToLocalDateTime(dateAfter));        
    }

    @Test
    void getUsersByCreatedAtBefore_WhenNoUserFound_ShouldReturnEmptyList() {
        // Arrange
        when(roleRepository.findByUser_Username(adminUsername)).thenReturn(Arrays.asList(adminRole));
        when(userRepository.findByCreatedAtBeforeAndAfter(convertToLocalDateTime(dateBefore), convertToLocalDateTime(dateAfter))).thenReturn(Collections.emptyList());

        // Act
        List<UserDTO> result = userService.getUsersByCreatedAtBeforeAndAfter(adminUsername, dateBefore, dateAfter);

        // Assert
        assertEquals(Collections.emptyList(), result);
        verify(userRepository, times(1)).findByCreatedAtBeforeAndAfter(convertToLocalDateTime(dateBefore), convertToLocalDateTime(dateAfter));
    }
    
    @Test
    void getUsersByCreatedAtBefore_WhenInvalidDateTimeFormat_ShouldThrowException() {
        // Arrange
        String currentUsername = "admin";
        when(roleRepository.findByUser_Username(currentUsername))
                .thenReturn(Collections.singletonList(createAdminRole()));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.getUsersByCreatedAtBeforeAndAfter(currentUsername, "invalid-date", "2025-02-20"));
        assertEquals(ErrorCode.INVALID_DATE_FORMAT.getMessage(), exception.getMessage());
    }
    
    @Test
    void getUsersByCreatedAtBefore_WhenCurrentUserIsNotAdmin_ShouldThrowException() {
        // Arrange
        String currentUsername = "user";
        when(roleRepository.findByUser_Username(currentUsername))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.getUsersByCreatedAtBeforeAndAfter(currentUsername, "2025-02-20", "2025-02-19"));
        assertEquals(ErrorCode.UNAUTHORIZED_ACCESS.getMessage(), exception.getMessage());
    }

    private LocalDateTime convertToLocalDateTime(String time) {         
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(time, formatter).atStartOfDay();
        } catch (DateTimeParseException ex)
        {
            throw new ValidationException(String.format("Invalid date time format, please use yyyy-MM-dd"));
        }
    }

    private User createUser(String username) {
        User user = new User();
        user.setId(java.util.UUID.randomUUID().toString());
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPassword("password");
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setCreatedAtZone(now.atZone(ZoneId.systemDefault()));
        user.setUpdatedAt(now);
        return user;
    }

    private Role createAdminRole() {
        Role role = new Role();
        role.setId(java.util.UUID.randomUUID().toString());
        role.setRolecode("ADMIN");
        role.setRoletype("SYSTEM");
        LocalDateTime now = LocalDateTime.now();
        role.setCreatedAt(now);
        role.setCreatedAtZone(now.atZone(ZoneId.systemDefault()));
        role.setUpdatedAt(now);
        return role;
    }
} 