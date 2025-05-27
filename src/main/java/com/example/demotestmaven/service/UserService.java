package com.example.demotestmaven.service;

import com.example.demotestmaven.dto.UserDTO;
import com.example.demotestmaven.dto.RoleDTO;
import com.example.demotestmaven.entity.User;
import com.example.demotestmaven.repository.UserRepository;
import com.example.demotestmaven.repository.RoleRepository;
import com.example.demotestmaven.exception.ResourceNotFoundException;
import com.example.demotestmaven.exception.UnauthorizedException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demotestmaven.entity.Role;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.demotestmaven.exception.ValidationException;
import com.example.demotestmaven.exception.ApiErrorType;
import com.example.demotestmaven.exception.ApiException;
import com.example.demotestmaven.exception.BusinessException;
import com.example.demotestmaven.exception.ErrorCode;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final DataFormatter dataFormatter = new DataFormatter();

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers(String currentUsername) {
        if (!isCurrentUserAdmin(currentUsername)) {
            throw new ApiException(ApiErrorType.FORBIDDEN_OPERATION);
        }
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
                .orElseThrow(() -> new  ApiException(ApiErrorType.USER_NOT_FOUND, username));//BusinessException(ErrorCode.USER_NOT_FOUND, username));
    }

    // Get users by createdAt before a given date time

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByCreatedAtBeforeAndAfter(String currentUsername, String timeBefore, String timeAfter) {        
                
        if (!isCurrentUserAdmin(currentUsername)) {
            throw new ApiException(ApiErrorType.FORBIDDEN_OPERATION);
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
            throw new ApiException(ApiErrorType.FORBIDDEN_OPERATION);
        }

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ApiException(ApiErrorType.USER_NOT_FOUND, currentUsername));
        
        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new ApiException(ApiErrorType.USER_NOT_FOUND, targetUsername));

        // Check permissions
        if (!canEditUser(currentUser, targetUser)) {
            throw new ApiException(ApiErrorType.FORBIDDEN_OPERATION);
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

    @Transactional
    public UserDTO createUser(String currentUsername, UserDTO userDTO) {
        if (userDTO == null) {
            throw new ApiException(ApiErrorType.USER_INVALID_INPUT);
        }

        if (!isCurrentUserAdmin(currentUsername)) {
            throw new ApiException(ApiErrorType.FORBIDDEN_OPERATION);
        }

        validateUserCreate(userDTO);

        User newUser = new User();
        newUser.setUsername(userDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setEmail(userDTO.getEmail());   

        newUser = updateUserRoles(newUser, userDTO.getRoles());
        
        return convertToDTO(userRepository.save(newUser));
    }

    @Transactional
    public List<UserDTO> importUsersFromExcel(InputStream file) throws IOException {
        List<User> users = new LinkedList<>();        

        Workbook  workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0);
        List<String> usernameContainer = new ArrayList<>();
        List<String> emailContainer = new ArrayList<>();
                
        //Iterator<Row> rowIterator = sheet.iterator();
        for (Row row : sheet) { 
            if ((row.getRowNum() == 0) || (isRowEmpty(row))) {
                continue;   
            }
            String username = getCellValue(row, 0);    
            String password = getCellValue(row, 1);
            String email = getCellValue(row, 2);
            String rolecode = getCellValue(row, 3);
            String roletype = getCellValue(row, 4);
            // User user = new User();

            User user = checkMultipleRole(username, password, email, rolecode, roletype, usernameContainer, emailContainer, users);
 
            validateUserCreate(convertToDTO(user));
        }    
        userRepository.saveAll(users);
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private User checkMultipleRole(String username, String password, String email, String rolecode, String roletype, 
                                    List<String> usernameContainer, List<String> emailContainer, List<User> users) {
        if (!usernameContainer.contains(username)) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);

            Role role = new Role();
            role.setRolecode(rolecode);
            role.setRoletype(roletype);

            role.setUser(user);
            user.getRoles().add(role);

            users.add(user);
            usernameContainer.add(username);
            emailContainer.add(email);
            return user;
        } else {
            int index = usernameContainer.indexOf(username);
            User userOld = users.get(index);
            
            if (userOld.getRoles().stream()
                                    .anyMatch(roleOld -> roleOld.getRolecode().equals(rolecode))) {
                    throw new ApiException(ApiErrorType.USER_ROLE_ALREADY_EXISTS, username);
            }

            if (!passwordEncoder.matches(password, userOld.getPassword())) {
                throw new ApiException(ApiErrorType.USER_PASSWORD_MISMATCH, username);
            }

            if (!userOld.getEmail().equals(email)) {
                throw new ApiException(ApiErrorType.USER_EMAIL_MISMATCH, username);
            }

            Role addRole = new Role();
            addRole.setRolecode(rolecode);
            addRole.setRoletype(roletype);
    
            addRole.setUser(userOld);
            userOld.getRoles().add(addRole);
            
            users.add(userOld);
            return userOld;
        }
    }

    private String getCellValue(Row row, int columnIndex) {
        return dataFormatter.formatCellValue(row.getCell(columnIndex)).trim();
    }


    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
    
        int lastColumn = row.getLastCellNum(); // may return -1 for empty row
        if (lastColumn < 0) return true;
    
        for (int c = row.getFirstCellNum(); c < lastColumn; c++) {
            Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = dataFormatter.formatCellValue(cell).trim();
                if (!value.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
    

    private void validateUserCreate(UserDTO userDTO) {

        if (!StringUtils.hasText(userDTO.getUsername())) {
            throw new ApiException(ApiErrorType.USER_INVALID_USERNAME);
        }

        if (!StringUtils.hasText(userDTO.getPassword())) {      
            throw new ApiException(ApiErrorType.USER_INVALID_PASSWORD);
        }

        if (!StringUtils.hasText(userDTO.getEmail())) {
            throw new ApiException(ApiErrorType.USER_INVALID_EMAIL);
        }

        if (!userDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ApiException(ApiErrorType.USER_INVALID_EMAIL);
        }

        if (userDTO.getPassword().length() < 8) {
            throw new ApiException(ApiErrorType.USER_INVALID_PASSWORD);
        }

        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new ApiException(ApiErrorType.USER_ALREADY_EXISTS);   
        }

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new ApiException(ApiErrorType.USER_EMAIL_ALREADY_EXISTS);
        }



        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            for (RoleDTO role : userDTO.getRoles()) {
                if (!StringUtils.hasText(role.getRolecode())) {
                    throw new ApiException(ApiErrorType.USER_ROLE_CODE_INVALID);
                }
            }
        }

        if (userDTO.getRoles() == null) {
            throw new ApiException(ApiErrorType.ROLE_REQUIRED_FIELD_MISSING, userDTO.getUsername());
        }

        if (userDTO.getRoles().stream().anyMatch(role -> role.getRoletype() == null)) {
            throw new ApiException(ApiErrorType.USER_ROLE_TYPE_INVALID);
        }
        


    }

    private User updateUserRoles(User user, List<RoleDTO> newRoles) {
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
                throw new ApiException(ApiErrorType.USER_ROLE_DUPLICATE, roleDTO.getRolecode());      
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
        return user;
    }

    private void validateUserUpdate(String currentUsername, String targetUsername, UserDTO userDTO) {
        // Validate current username
        if (!StringUtils.hasText(currentUsername)) {
            throw new ApiException(ApiErrorType.USER_INVALID_USERNAME);
        }

        // Validate target username
        if (!StringUtils.hasText(targetUsername)) {
            throw new ApiException(ApiErrorType.USER_INVALID_USERNAME);
        }

        // Validate userDTO
        if (userDTO == null) {
            throw new ApiException(ApiErrorType.USER_INVALID_INPUT);
        }

        // Validate email format if provided
        if (userDTO.getEmail() != null && !userDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ApiException(ApiErrorType.USER_INVALID_EMAIL);
        }

        // Validate password length if provided
        if (userDTO.getPassword() != null && userDTO.getPassword().length() < 8) {
            throw new ApiException(ApiErrorType.USER_INVALID_PASSWORD);
        }

        // Validate target user is not the UserDTO itself
        if (!userDTO.getUsername().equals(targetUsername)) {
            throw new ApiException(ApiErrorType.USER_INVALID_INPUT);    
        }

        // Validate roles if provided
        if (userDTO.getRoles() != null) {
            for (RoleDTO role : userDTO.getRoles()) {
                if (!StringUtils.hasText(role.getRolecode())) {
                    throw new ApiException(ApiErrorType.USER_ROLE_CODE_INVALID);
                }
                if (!StringUtils.hasText(role.getRoletype())) {
                    throw new ApiException(ApiErrorType.USER_ROLE_TYPE_INVALID);
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
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setCreatedAtLocal(user.getCreatedAtLocal());
        
        
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
                roleDTO.setUpdatedAt(role.getUpdatedAt());
                roleDTO.setCreatedAtLocal(role.getCreatedAtLocal());
                return roleDTO;
            })
            .collect(Collectors.toList());
        
        // dto.setRoletypes(roletypes);
        dto.setRoles(roles);
        
        return dto;
    }

    private void validateTimeBeforeAndAfter(String timeBefore, String timeAfter) {
        if (!StringUtils.hasText(timeBefore) || !StringUtils.hasText(timeAfter)) {
            throw new ApiException(ApiErrorType.USER_INVALID_INPUT);
        }
    }

    boolean isCurrentUserAdmin(String username) {
        List<Role> roles = roleRepository.findByUser_Username(username);
        return roles.stream()
                .anyMatch(role -> "ADMIN".equals(role.getRolecode()));
    }

    private LocalDateTime convertToLocalDateTime(String time) {         
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(time, formatter).atStartOfDay();
        } catch (DateTimeParseException ex) {
            throw new ApiException(ApiErrorType.USER_INVALID_INPUT);
        }
    }
} 