package com.example.demotestmaven.service;

import com.example.demotestmaven.dto.UserDTO;
import com.example.demotestmaven.dto.RoleDTO;
import com.example.demotestmaven.entity.User;
import com.example.demotestmaven.repository.UserRepository;
import com.example.demotestmaven.repository.RoleRepository;
import com.example.demotestmaven.exception.ResourceNotFoundException;
import com.example.demotestmaven.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demotestmaven.entity.Role;
import org.springframework.util.StringUtils;
import com.example.demotestmaven.exception.ValidationException;
import com.example.demotestmaven.exception.BusinessException;
import com.example.demotestmaven.exception.ErrorCode;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashSet;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        // List<User> users = userRepository.findAll();
        List<User> users = userRepository.findAllWithRoles();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDTO)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, username));
    }

    // Get users by createdAt before a given date time

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByCreatedAtBeforeAndAfter(String currentUsername, String timeBefore, String timeAfter) {        
                
        if (!isCurrentUserAdmin(currentUsername)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        validateTimeBeforeAndAfter(timeBefore, timeAfter);
        LocalDateTime dateTimeBefore = convertToLocalDateTime(timeBefore);
        LocalDateTime dateTimeAfter = convertToLocalDateTime(timeAfter);
        
        List<User> users = userRepository.findByCreatedAtBeforeAndAfter(dateTimeBefore, dateTimeAfter);      

        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateUser(String currentUsername, String targetUsername, UserDTO userDTO) {
        // Validate input parameters
        validateUserUpdate(currentUsername, targetUsername, userDTO);
        
        if (!isCurrentUserAdmin(currentUsername) && (!currentUsername.equals(targetUsername))) {
            throw new BusinessException(ErrorCode.FORBIDDEN_OPERATION);
        }

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, currentUsername));
        
        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, targetUsername));

        // Check permissions
        if (!canEditUser(currentUser, targetUser)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_OPERATION);
        }

        // Update user information
        if (userDTO.getEmail() != null) {
            targetUser.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPassword() != null) {
            targetUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        // Update roles if provided
        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            updateUserRoles(targetUser, userDTO.getRoles());
        }

        return convertToDTO(userRepository.save(targetUser));
    }

    private void updateUserRoles(User user, List<RoleDTO> newRoles) {
        // Track unique rolecodes to prevent duplicates
        Set<String> uniqueRolecodes = new HashSet<>();

        // First, remove roles that are not in the new roles list
        user.getRoles().removeIf(role -> {
            boolean shouldKeep = newRoles.stream()
                .anyMatch(newRole -> newRole.getRolecode().equals(role.getRolecode()));
            return !shouldKeep;
        });

        // Add or update roles
        for (RoleDTO roleDTO : newRoles) {
            // Check for duplicate rolecodes
            if (!uniqueRolecodes.add(roleDTO.getRolecode())) {
                throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, 
                    "Duplicate role code: %s. A user cannot have the same role multiple times.", roleDTO.getRolecode());
            }

            // Check if role already exists
            boolean roleExists = user.getRoles().stream()
                .anyMatch(role -> role.getRolecode().equals(roleDTO.getRolecode()));

            if (!roleExists) {
                Role role = new Role();
                role.setUser(user);
                role.setRolecode(roleDTO.getRolecode());
                role.setRoletype(roleDTO.getRoletype());
                user.getRoles().add(role);
            }
        }
    }

    private void validateUserUpdate(String currentUsername, String targetUsername, UserDTO userDTO) {
        // Validate current username
        if (!StringUtils.hasText(currentUsername)) {
            throw new BusinessException(ErrorCode.INVALID_USERNAME);
        }

        // Validate target username
        if (!StringUtils.hasText(targetUsername)) {
            throw new BusinessException(ErrorCode.INVALID_USERNAME);
        }

        // Validate userDTO
        if (userDTO == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        // Validate email format if provided
        if (userDTO.getEmail() != null && !userDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL);
        }

        // Validate password length if provided
        if (userDTO.getPassword() != null && userDTO.getPassword().length() < 8) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // Validate target user is not the UserDTO itself
        if (!userDTO.getUsername().equals(targetUsername)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Target user is not the same as needed update user");
        }

        // Validate roles if provided
        if (userDTO.getRoles() != null) {
            for (RoleDTO role : userDTO.getRoles()) {
                if (!StringUtils.hasText(role.getRolecode())) {
                    throw new BusinessException(ErrorCode.INVALID_ROLE, "Role code is required for each role");
                }
                if (!StringUtils.hasText(role.getRoletype())) {
                    throw new BusinessException(ErrorCode.INVALID_ROLE, "Role type is required for each role");
                }
            }
        }
    }

    private boolean canEditUser(User currentUser, User targetUser) {
        // User can edit their own information
        if (currentUser.getUsername().equals(targetUser.getUsername())) {
            return true;
        }

        // Check if current user has admin or manager role
        return roleRepository.findByUser_Username(currentUser.getUsername()).stream()
                .anyMatch(role -> role.getRolecode().equals("ADMIN") || role.getRolecode().equals("MANAGER"));
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setCreatedAtZone(user.getCreatedAtZone());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        Set<String> roletypes = user.getRoles().stream()
            .map(role -> role.getRoletype())
            .collect(Collectors.toSet());
        
        dto.setRoletypes(roletypes);
        
        List<RoleDTO> roles = user.getRoles().stream()
            .map(role -> {
                RoleDTO roleDTO = new RoleDTO();
                roleDTO.setId(role.getId());
                roleDTO.setUsername(role.getUser().getUsername());
                roleDTO.setRolecode(role.getRolecode());
                roleDTO.setRoletype(role.getRoletype());
                roleDTO.setCreatedAt(role.getCreatedAt());
                roleDTO.setCreatedAtZone(role.getCreatedAtZone());
                roleDTO.setUpdatedAt(role.getUpdatedAt());
                return roleDTO;
            })
            .collect(Collectors.toList());
        
        dto.setRoles(roles);
        
        return dto;
    }

    private void validateTimeBeforeAndAfter(String timeBefore, String timeAfter) {
        if (!StringUtils.hasText(timeBefore) || !StringUtils.hasText(timeAfter)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
    }

    private boolean isCurrentUserAdmin(String username) {
        List<Role> roles = roleRepository.findByUser_Username(username);
        return roles.stream()
                .anyMatch(role -> "ADMIN".equals(role.getRolecode()));
    }

    private LocalDateTime convertToLocalDateTime(String time) {         
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(time, formatter).atStartOfDay();
        } catch (DateTimeParseException ex) {
            throw new BusinessException(ErrorCode.INVALID_DATE_FORMAT);
        }
    }
} 