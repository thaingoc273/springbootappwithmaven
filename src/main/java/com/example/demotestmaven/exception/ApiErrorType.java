package com.example.demotestmaven.exception;

import java.util.IllegalFormatException;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ApiErrorType {
    //User error
    USER_NOT_FOUND("User not found with username: %s", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("User already exists: %s", HttpStatus.BAD_REQUEST),
    USER_INVALID_INPUT("Invalid input", HttpStatus.BAD_REQUEST),
    USER_INVALID_USERNAME("Invalid username %s", HttpStatus.BAD_REQUEST),
    USER_INVALID_PASSWORD("Invalid password %s", HttpStatus.BAD_REQUEST),
    USER_INVALID_EMAIL("Invalid email %s", HttpStatus.BAD_REQUEST),
    USER_EMAIL_ALREADY_EXISTS("Email already exists for email: %s", HttpStatus.BAD_REQUEST),
    USER_ROLE_INVALID("Invalid role: %s", HttpStatus.BAD_REQUEST),
    USER_ROLE_TYPE_INVALID("Invalid role type %s", HttpStatus.BAD_REQUEST),
    USER_ROLE_CODE_INVALID("Invalid role code %s", HttpStatus.BAD_REQUEST),
    USER_ROLE_DUPLICATE("Duplicate role code: %s. A user cannot have the same role multiple times.", HttpStatus.BAD_REQUEST),
    USER_PASSWORD_MISMATCH("Password mismatch for user: %s", HttpStatus.BAD_REQUEST),
    USER_EMAIL_MISMATCH("Email mismatch for user: %s", HttpStatus.BAD_REQUEST),    
    USER_OLD_ROLE_EXISTS("User already has role: %s", HttpStatus.BAD_REQUEST),
    USER_ROLE_ALREADY_EXISTS("User already has role: %s", HttpStatus.BAD_REQUEST),
    USER_ROLE_REQUIRED("User must have at least one role", HttpStatus.BAD_REQUEST),

    //Role error
    ROLE_NOT_FOUND("Role not found with name: %s", HttpStatus.NOT_FOUND),
    ROLE_ALREADY_EXISTS("Role already exists %s", HttpStatus.BAD_REQUEST),
    ROLE_INVALID_INPUT("Invalid input %s", HttpStatus.BAD_REQUEST),
    ROLE_REQUIRED_FIELD_MISSING("Required role for user %s is missing", HttpStatus.BAD_REQUEST),

    // Excel error
    USER_EXCEL_UNREADABLE_DATA("Unreadable data", HttpStatus.BAD_REQUEST),
    
    //Authorization error
    UNAUTHORIZED_ACCESS("Unauthorized access", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_OPERATION("Forbidden operation, only admin can access", HttpStatus.FORBIDDEN),

    //Internal server error
    INTERNAL_SERVER_ERROR("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    ;


    private final String message;
    private final HttpStatus status;

    ApiErrorType(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }   

    // public String getMessage() {
    //     return message;
    // }

    // public HttpStatus getStatus() {
    //     return status;
    // }
     public String getFormattedMessage(Object... args) {        
        try {
            return String.format(message, args);
        } catch (IllegalFormatException e) {
            return message; // fallback if formatting fails
        }
    }
}