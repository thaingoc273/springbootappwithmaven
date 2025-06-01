package com.example.demotestmaven.service;

import com.example.demotestmaven.dto.UserDTO;
import com.example.demotestmaven.dto.UserExcelFullResponseDTO;
import com.example.demotestmaven.dto.UserExcelRequestDTO;
import com.example.demotestmaven.constants.GlobalConstants;
import com.example.demotestmaven.dto.RoleDTO;
import com.example.demotestmaven.dto.UserExcelResponseDTO;
import com.example.demotestmaven.dto.UserExcelValidateDTO;
import com.example.demotestmaven.entity.User;
import com.example.demotestmaven.repository.UserRepository;
import com.example.demotestmaven.repository.RoleRepository;
import com.example.demotestmaven.exception.ApiErrorType;
import com.example.demotestmaven.exception.ApiException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demotestmaven.entity.Role;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AsyncUserService asyncUserService;

    private final DataFormatter dataFormatter = new DataFormatter();

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private static final String ROW_NUMBER_HEADER = "rownumber";
    private static final String USERNAME_HEADER = "username";
    private static final String PASSWORD_HEADER = "password";
    private static final String EMAIL_HEADER = "email";
    private static final String ROLECODE_HEADER = "rolecode";
    private static final String ROLETYPE_HEADER = "roletype";

    private static final String STATUS_SUCCESS = GlobalConstants.successStatus;
    private static final String STATUS_ERROR = GlobalConstants.errorStatus;

    private static final String MESSAGE_SUCCESS = "User created successfully";
    private static final String MESSAGE_ERROR = "User creation failed";
    private static final String VALID_SUCCESS = "User created successfully";
    private static final String VALID_ERROR = "User creation failed";  
    private static final String NO_NAME_FOUND = "No name found";

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
    public List<UserExcelFullResponseDTO> importUsersFromExcel(MultipartFile file) {    

        List<UserExcelFullResponseDTO> userExcelFullResponseDTOs = new ArrayList<>();       
        List<UserExcelRequestDTO> userExcelRequestDTOs = getUserExcelRequestDTOs(file);
        
        if (userExcelRequestDTOs.isEmpty()) {
            throw new ApiException(ApiErrorType.USER_EXCEL_READING_EMPTY_FILE);
        }

        logger.info("userExcelRequestDTOs: {}", userExcelRequestDTOs);
        logger.info("userExcelRequestDTOs size: {}", userExcelRequestDTOs.size());
        
        
        for (UserExcelRequestDTO userExcelRequestDTO : userExcelRequestDTOs) {

            // If row is not readable, return exception
            if ((userExcelRequestDTO.getUsername() == null) || (userExcelRequestDTO.getEmail() == null)||
                (userExcelRequestDTO.getRolecodes() == null) || (userExcelRequestDTO.getPassword() == null)||
                (userExcelRequestDTO.getRoletypes() == null)){
                throw new ApiException(ApiErrorType.USER_EXCEL_UNREADABLE_DATA);
            }
            
                        
            // Transform request to response
            UserExcelFullResponseDTO userExcelFullResponseDTO = transformUserExcelRequestDTO(userExcelRequestDTO);



            // Update users if success
            if (userExcelFullResponseDTO.getStatusUpdate() == STATUS_SUCCESS) {
               User user = createUserExcel(userExcelRequestDTO);
               asyncUserService.saveUsers(user); // Save user in a new thread
                // userRepository.save(user);
                // logger.info("Thread: {}", Thread.currentThread().getName());
                // logger.info("User saved: {}", user.getUsername());
                // try {
                //     Thread.sleep((long) (Math.random() * 10000));
                // } catch (InterruptedException e) {
                //     e.printStackTrace();
                // }
            }

            // Add to list
            userExcelFullResponseDTOs.add(userExcelFullResponseDTO);
        }

        return userExcelFullResponseDTOs;
    }

    private User createUserExcel(UserExcelRequestDTO userExcelRequestDTO) {
        User user = new User();
        user.setUsername(userExcelRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userExcelRequestDTO.getPassword()));
        user.setEmail(userExcelRequestDTO.getEmail());
        String[] rolecodes = userExcelRequestDTO.getRolecodes().split(",");
        String[] roletypes = userExcelRequestDTO.getRoletypes().split(",");
        for (int i = 0; i < rolecodes.length; i++) {
            Role role = new Role();
            role.setRolecode(rolecodes[i]);
            role.setRoletype(roletypes[i]);
            role.setUser(user);
            user.getRoles().add(role);
        }
        return user;
    }

    private List<UserExcelRequestDTO> getUserExcelRequestDTOs(MultipartFile file) {
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            List<UserExcelRequestDTO> userExcelRequestDTOs = new ArrayList<>();
            Map<String, Integer> excelMappingHeader = new HashMap<>();
            
            for (Row row : sheet) {
                // Reading header row
                if (row.getRowNum() == 0) {
                    excelMappingHeader = getExcelMappingHeader(row);
                    continue;
                }
                // Skip empty rows
                if (isRowEmpty(row)) {
                    continue;
                }
                UserExcelRequestDTO userExcelRequestDTO = convertToUserExcelRequestDTO(row, excelMappingHeader);
                userExcelRequestDTOs.add(userExcelRequestDTO);

                }
            return userExcelRequestDTOs;
        } catch (Exception e) {
            throw new ApiException(ApiErrorType.USER_EXCEL_UNREADABLE_DATA);
        }
    }

    private Sheet getSheet(MultipartFile file) {
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            return workbook.getSheetAt(0);
        } catch (IOException e) {
            throw new ApiException(ApiErrorType.USER_EXCEL_UNREADABLE_DATA);
        }
    }

    private Map<String, Integer> getExcelMappingHeader(Row row) {
        Map<String, Integer> excelMappingHeader = new HashMap<>();
        for (int i = 0; i < row.getLastCellNum(); i++) {
            excelMappingHeader.put(getCellValue(row, i), i);
        }
        return excelMappingHeader;
    }

    
    private UserExcelRequestDTO convertToUserExcelRequestDTO(Row row, Map<String, Integer> excelMappingHeader) {
        UserExcelRequestDTO userExcelRequestDTO = new UserExcelRequestDTO();
        userExcelRequestDTO.setRowNumber(getCellValuefromExcelMappingHeader(row, excelMappingHeader, ROW_NUMBER_HEADER));     
        userExcelRequestDTO.setUsername(getCellValuefromExcelMappingHeader(row, excelMappingHeader, USERNAME_HEADER));
        userExcelRequestDTO.setPassword(getCellValuefromExcelMappingHeader(row, excelMappingHeader, PASSWORD_HEADER));
        userExcelRequestDTO.setEmail(getCellValuefromExcelMappingHeader(row, excelMappingHeader, EMAIL_HEADER));
        userExcelRequestDTO.setRolecodes(getCellValuefromExcelMappingHeader(row, excelMappingHeader, ROLECODE_HEADER));
        userExcelRequestDTO.setRoletypes(getCellValuefromExcelMappingHeader(row, excelMappingHeader, ROLETYPE_HEADER));
        return userExcelRequestDTO;
    }

    private String getCellValuefromExcelMappingHeader(Row row, Map<String, Integer> excelMappingHeader, String header) {
        Integer columnIndex = excelMappingHeader.get(header);
        if (columnIndex == null) {
            return null;
        }
        return getCellValue(row, columnIndex).trim();
    }

    private boolean validateUserExcelRequestDTO(UserExcelRequestDTO userExcelRequestDTO, Map<String, User> users) {
        String username = userExcelRequestDTO.getUsername();
        String password = userExcelRequestDTO.getPassword();
        String email = userExcelRequestDTO.getEmail();
        String rolecodes = userExcelRequestDTO.getRolecodes();
        String roletypes = userExcelRequestDTO.getRoletypes();

        // New user
        if (!users.containsKey(username)) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);

            Role role = new Role();
            role.setRolecode(rolecodes);
            role.setRoletype(roletypes);

            role.setUser(user);
            user.getRoles().add(role);
            if (validateNewUserExcelImport(user)) {                
                return true;
            }
        }

        // Existing user        
        User existingUser = users.get(username);
        if (validateExistingUserExcelImport(existingUser, userExcelRequestDTO)) {
            return true;
        }

        return false;
    }

    private boolean validateExistingUserExcelImport(User existingUser, UserExcelRequestDTO userExcelRequestDTO) {
        if (existingUser.getRoles().stream()
                .anyMatch(roleOld -> roleOld.getRolecode().equals(userExcelRequestDTO.getRolecodes()))) {
        // throw new ApiException(ApiErrorType.USER_ROLE_ALREADY_EXISTS, username);
        return false;
        }

        if (!passwordEncoder.matches(userExcelRequestDTO.getPassword(), existingUser.getPassword())) {      
        // throw new ApiException(ApiErrorType.USER_PASSWORD_MISMATCH, username);
        return false;
        }

        if (!existingUser.getEmail().equals(userExcelRequestDTO.getEmail())) {
        // throw new ApiException(ApiErrorType.USER_EMAIL_MISMATCH, username);
        return false;
        }
        return true;
    }

    private boolean validateNewUserExcelImport(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return false;
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return false;
        }
        if (user.getPassword().length() < 8) {
            return false;
        }
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return false;
        }
        if (user.getRoles().isEmpty()) {
            return false;
        }
        if (user.getRoles().stream().anyMatch(role -> role.getRoletype() == null)) {
            return false;
        }   
        if (user.getRoles().stream().anyMatch(role -> role.getRolecode() == null)) {
            return false;
        }
        if (user.getRoles().stream().anyMatch(role -> role.getRolecode().isEmpty())) {
            return false;
        }
        if (user.getRoles().stream().anyMatch(role -> role.getRoletype().isEmpty())) {
            return false;
        }
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (Role role : user.getRoles()) {
                if (!StringUtils.hasText(role.getRolecode())) {
                    return false;
                }
                if (!StringUtils.hasText(role.getRoletype())) {
                    return false;
                }
            }
        }
        return true;
        
    }

    private UserExcelFullResponseDTO transformUserExcelRequestDTO(UserExcelRequestDTO userExcelRequestDTO) {
        
        UserExcelFullResponseDTO userExcelFullResponseDTO = createInitialResponse(userExcelRequestDTO);
        List<String> messagesError = new ArrayList<>();

        // Check empty fields
        List<String> messagesForCheckingEmptyFields = getMessagesForCheckingEmptyFields(userExcelRequestDTO);
        if (!messagesForCheckingEmptyFields.isEmpty()) {
            userExcelFullResponseDTO.setStatusUpdate(STATUS_ERROR);
            messagesError.addAll(messagesForCheckingEmptyFields);           
        }

        // Check user uniqueness
        List<String> messagesForCheckingUserUniqueness = getMessagesForCheckingUserUniqueness(userExcelRequestDTO);
        if (!messagesForCheckingUserUniqueness.isEmpty()) {
            userExcelFullResponseDTO.setStatusUpdate(STATUS_ERROR);
            messagesError.addAll(messagesForCheckingUserUniqueness);           
        }

        // Check email and password
        List<String> messagesForCheckingEmailAndPassword = getMessagesForCheckingEmailAndPassword(userExcelRequestDTO);
        if (!messagesForCheckingEmailAndPassword.isEmpty()) {
            userExcelFullResponseDTO.setStatusUpdate(STATUS_ERROR);
            messagesError.addAll(messagesForCheckingEmailAndPassword);           
        }

        // Check roles
        List<String> messagesForCheckingRoles = getMessagesForCheckingRoles(userExcelRequestDTO);
        if (!messagesForCheckingRoles.isEmpty()) {
            userExcelFullResponseDTO.setStatusUpdate(STATUS_ERROR);
            messagesError.addAll(messagesForCheckingRoles);           
        }
        userExcelFullResponseDTO.setMessages(messagesError);

        return userExcelFullResponseDTO;
    }

    private UserExcelFullResponseDTO createInitialResponse(UserExcelRequestDTO requestDTO) {
        UserExcelFullResponseDTO response = new UserExcelFullResponseDTO();
        if ((requestDTO.getRowNumber() != null) && (!requestDTO.getRowNumber().isEmpty())) {
            response.setRowNumber(requestDTO.getRowNumber());
        }
        else {
            response.setRowNumber(GlobalConstants.MISSING_ROW_NUMBER);
        }
        response.setUsername(requestDTO.getUsername());
        response.setStatusUpdate(STATUS_SUCCESS);
        return response;
    }

    private List<String> getMessagesForCheckingEmptyFields(UserExcelRequestDTO requestDTO) {
        List<String> messages = new ArrayList<>();
        Map<String, String> fields = Map.of(
            ROW_NUMBER_HEADER, requestDTO.getRowNumber(),
            USERNAME_HEADER, requestDTO.getUsername(),
            EMAIL_HEADER, requestDTO.getEmail(),
            ROLECODE_HEADER, requestDTO.getRolecodes(),
            ROLETYPE_HEADER, requestDTO.getRoletypes(),
            PASSWORD_HEADER, requestDTO.getPassword()
        );

       fields.forEach((fieldHeader, fieldValue) -> {
        if (fieldValue.isEmpty()) {
            messages.add(String.format(ApiErrorType.USER_EXCEL_MISSING_DATA.getFormattedMessage(fieldHeader)));
        }
       });
       return messages;
    }

    

    private List<String> getMessagesForCheckingUserUniqueness(UserExcelRequestDTO requestDTO) {
        List<String> messages = new ArrayList<>();
        if (userRepository.findByUsername(requestDTO.getUsername()).isPresent()) {
            messages.add(String.format(ApiErrorType.USER_ALREADY_EXISTS.getFormattedMessage(requestDTO.getUsername())));
        }
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            messages.add(String.format(ApiErrorType.USER_EMAIL_ALREADY_EXISTS.getFormattedMessage(requestDTO.getEmail())));
        }
        return messages;
    }

    private List<String> getMessagesForCheckingEmailAndPassword(UserExcelRequestDTO requestDTO) {
        List<String> messages = new ArrayList<>();
        if (!requestDTO.getPassword().isEmpty() && requestDTO.getPassword().length() < 8) {
            messages.add(String.format(ApiErrorType.USER_INVALID_PASSWORD.getFormattedMessage(requestDTO.getPassword())));
        }
        if (!requestDTO.getEmail().isEmpty() && !requestDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            messages.add(String.format(ApiErrorType.USER_INVALID_EMAIL.getFormattedMessage(requestDTO.getEmail())));
        }
        return messages;
    }

    private List<String> getMessagesForCheckingRoles(UserExcelRequestDTO requestDTO) {
        List<String> messages = new ArrayList<>();

        List<String> rolecodes = Arrays.asList(requestDTO.getRolecodes().split(","));
        List<String> roletypes = Arrays.asList(requestDTO.getRoletypes().split(","));

        if ((rolecodes.size() != roletypes.size()) && (rolecodes.size() != 0)) {
            messages.add(ApiErrorType.ROLE_CODE_TYPE_MISMATCH.getMessage());
        }

        if (rolecodes.stream().anyMatch(String::isEmpty)) {
            messages.add(ApiErrorType.ROLE_CODE_EMPTY_SUBSTRING.getMessage());
        }
        if (roletypes.stream().anyMatch(String::isEmpty)) {
            messages.add(ApiErrorType.ROLE_TYPE_EMPTY_SUBSTRING.getMessage());
        }
        if (!(rolecodes.size() == new HashSet<>(rolecodes).size())) {
            messages.add(ApiErrorType.ROLE_CODE_DUPLICATE.getMessage());
        }
        return messages;
    }


    private String getErrorMessage(String fieldString, String fieldHeader) {
        if (fieldString.isEmpty()) {
            return String.format(ApiErrorType.USER_EXCEL_MISSING_DATA.getMessage(), fieldHeader);
        }

        return null;
    }

    private String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return (cell != null) ? dataFormatter.formatCellValue(cell) : "";
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

    private UserExcelFullResponseDTO validateExistingExcelUser(User existingUser, UserExcelRequestDTO userExcelRequestDTO) {
        List<String> messages = new ArrayList<>(); //validSuccess;
        messages.add(VALID_SUCCESS);
        if (existingUser.getRoles().stream()
                .anyMatch(roleOld -> roleOld.getRolecode().equals(userExcelRequestDTO.getRolecodes()))) {
        messages.set(0, VALID_ERROR);     
        messages.add(ApiErrorType.USER_ROLE_ALREADY_EXISTS.getMessage());

        }

        if (!passwordEncoder.matches(userExcelRequestDTO.getPassword(), existingUser.getPassword())) {
        messages.set(0, VALID_ERROR);     
        messages.add(ApiErrorType.USER_PASSWORD_MISMATCH.getMessage());
        }

        if (!existingUser.getEmail().equals(userExcelRequestDTO.getEmail())) {
        messages.set(0, VALID_ERROR);     
        messages.add(ApiErrorType.USER_EMAIL_MISMATCH.getMessage());
        }
        UserExcelFullResponseDTO userExcelFullResponseDTO = new UserExcelFullResponseDTO();
        userExcelFullResponseDTO.setUsername(existingUser.getUsername());
        if (messages.get(0) != VALID_SUCCESS) {
            userExcelFullResponseDTO.setMessages(messages);
            userExcelFullResponseDTO.setStatusUpdate(STATUS_ERROR);
            return userExcelFullResponseDTO;
        }
        userExcelFullResponseDTO.setMessages(List.of(VALID_SUCCESS));
        userExcelFullResponseDTO.setStatusUpdate(STATUS_SUCCESS);  
        return userExcelFullResponseDTO;
    }



    private UserExcelFullResponseDTO validateNewExcelUser(UserExcelRequestDTO userExcelRequestDTO) {
        List<String> messages = new ArrayList<>(); //validSuccess;
        messages.add(VALID_SUCCESS);
        if (userRepository.findByUsername(userExcelRequestDTO.getUsername().trim()).isPresent()) {
            messages.add(String.format(ApiErrorType.USER_ALREADY_EXISTS.getMessage(), userExcelRequestDTO.getUsername()));
            messages.set(0, VALID_ERROR);
        }
        if (userRepository.findByEmail(userExcelRequestDTO.getEmail().trim()).isPresent()) {
            messages.add(String.format(ApiErrorType.USER_EMAIL_ALREADY_EXISTS.getMessage(), userExcelRequestDTO.getEmail(), userExcelRequestDTO.getEmail()));
            messages.set(0, VALID_ERROR);
        }
      
        if (userExcelRequestDTO.getPassword().length() < 8) {
            messages.add(String.format(ApiErrorType.USER_INVALID_PASSWORD.getMessage(), userExcelRequestDTO.getPassword()));
            messages.set(0, VALID_ERROR);
        }
        if (!userExcelRequestDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            messages.add(String.format(ApiErrorType.USER_INVALID_EMAIL.getMessage(), userExcelRequestDTO.getEmail()));
            messages.set(0, VALID_ERROR);
        }
        if (userExcelRequestDTO.getRolecodes().isEmpty()) {
            messages.add(String.format(ApiErrorType.USER_ROLE_REQUIRED.getMessage(), userExcelRequestDTO.getUsername()));
            messages.set(0, VALID_ERROR);
        }
        if (userExcelRequestDTO.getRoletypes().isEmpty()) {
            messages.add(String.format(ApiErrorType.USER_ROLE_TYPE_INVALID.getMessage(), userExcelRequestDTO.getUsername()));
            messages.set(0, VALID_ERROR);
        }   

        if (userExcelRequestDTO.getRolecodes() == null) {
            messages.add(String.format(ApiErrorType.USER_ROLE_CODE_INVALID.getMessage(), userExcelRequestDTO.getUsername()));
            messages.set(0, VALID_ERROR);
        }
        if (userExcelRequestDTO.getRoletypes() == null) {
            messages.add(String.format(ApiErrorType.USER_ROLE_TYPE_INVALID.getMessage(), userExcelRequestDTO.getUsername()));
            messages.set(0, VALID_ERROR);
        }

        if (userExcelRequestDTO.getRolecodes() != null && userExcelRequestDTO.getRoletypes() != null) {

            if (!StringUtils.hasText(userExcelRequestDTO.getRolecodes())) {
                messages.add(String.format(ApiErrorType.USER_ROLE_CODE_INVALID.getMessage(), userExcelRequestDTO.getUsername()));
                messages.set(0, VALID_ERROR);
            }
            if (!StringUtils.hasText(userExcelRequestDTO.getRoletypes())) {
                messages.add(String.format(ApiErrorType.USER_ROLE_TYPE_INVALID.getMessage(), userExcelRequestDTO.getUsername()));
                messages.set(0, VALID_ERROR);
            }
            
        }
        UserExcelFullResponseDTO userExcelFullResponseDTO = new UserExcelFullResponseDTO();
        if (messages.get(0) != VALID_SUCCESS) {            
            userExcelFullResponseDTO.setUsername(userExcelRequestDTO.getUsername());
            userExcelFullResponseDTO.setMessages(messages);
            userExcelFullResponseDTO.setStatusUpdate(STATUS_ERROR);
            return userExcelFullResponseDTO;
        }
        userExcelFullResponseDTO.setUsername(userExcelRequestDTO.getUsername());
        userExcelFullResponseDTO.setMessages(List.of(VALID_SUCCESS));
        userExcelFullResponseDTO.setStatusUpdate(STATUS_SUCCESS);
        return userExcelFullResponseDTO;
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

    private UserExcelResponseDTO convertToUserExcelResponseDTO(User user) {
        UserExcelResponseDTO dto = new UserExcelResponseDTO();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setRoles(user.getRoles().stream()
            .map(role -> role.getRolecode())
            .collect(Collectors.toSet()));
        return dto;
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