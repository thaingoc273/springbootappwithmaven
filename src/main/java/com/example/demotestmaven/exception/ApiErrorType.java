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
    USER_MISSING_USERNAME("Username is missing", HttpStatus.BAD_REQUEST),
    USER_MISSING_PASSWORD("Password is missing", HttpStatus.BAD_REQUEST),
    USER_MISSING_EMAIL("Email is missing", HttpStatus.BAD_REQUEST),
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
    USER_ROLE_TYPE_EMPTY("Role type is empty", HttpStatus.BAD_REQUEST),
    USER_ROLE_CODE_EMPTY("Role code is empty", HttpStatus.BAD_REQUEST),
    USER_EXCEL_MISSING_DATA("Missing data for field %s", HttpStatus.BAD_REQUEST),
    USER_EXCEL_MISSING_EMAIL_DATA("Missing email data", HttpStatus.BAD_REQUEST),

    //Async user error
    ASYNC_USER_NOT_FOUND("Async user not found with username: %s", HttpStatus.NOT_FOUND),
    ASYNC_USER_ALREADY_EXISTS("Async user already exists: %s", HttpStatus.BAD_REQUEST),
    ASYNC_USER_INVALID_INPUT("Async invalid input", HttpStatus.BAD_REQUEST),
    ASYNC_USER_MISSING_USERNAME("Async username is missing", HttpStatus.BAD_REQUEST),
    ASYNC_USER_MISSING_PASSWORD("Async password is missing", HttpStatus.BAD_REQUEST),
    ASYNC_USER_MISSING_EMAIL("Async email is missing", HttpStatus.BAD_REQUEST),

    //Role error
    ROLE_NOT_FOUND("Role not found with name: %s", HttpStatus.NOT_FOUND),
    ROLE_ALREADY_EXISTS("Role already exists %s", HttpStatus.BAD_REQUEST),
    ROLE_INVALID_INPUT("Invalid input %s", HttpStatus.BAD_REQUEST),
    ROLE_REQUIRED_FIELD_MISSING("Required role for user %s is missing", HttpStatus.BAD_REQUEST),
    ROLE_CODE_TYPE_MISMATCH("Role code and type mismatch", HttpStatus.BAD_REQUEST),
    ROLE_CODE_MISSING("Role code is missing", HttpStatus.BAD_REQUEST),
    ROLE_CODE_EMPTY_SUBSTRING("One of the role codes in the list is empty", HttpStatus.BAD_REQUEST),
    ROLE_TYPE_MISSING("Role type is missing", HttpStatus.BAD_REQUEST),
    ROLE_TYPE_EMPTY_SUBSTRING("One of the role types in the list is empty", HttpStatus.BAD_REQUEST),
    ROLE_CODE_DUPLICATE("Role code is duplicate", HttpStatus.BAD_REQUEST),


    // Excel error
    USER_EXCEL_UNREADABLE_DATA("Unreadable data", HttpStatus.BAD_REQUEST),
    USER_EXCEL_READING_EMPTY_DATA("Reading empty data", HttpStatus.BAD_REQUEST),
    USER_EXCEL_READING_EMPTY_FILE("Reading empty file", HttpStatus.BAD_REQUEST),

    //Authorization error
    UNAUTHORIZED_ACCESS("Unauthorized access", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_OPERATION("Forbidden operation, only admin can access", HttpStatus.FORBIDDEN),

    //Internal server error
    INTERNAL_SERVER_ERROR("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),

    //Database errors
    DATABASE_CONNECTION_ERROR("Database connection error occurred", HttpStatus.SERVICE_UNAVAILABLE),
    DATABASE_TIMEOUT_ERROR("Database operation timed out", HttpStatus.REQUEST_TIMEOUT),
    DATABASE_CONSTRAINT_VIOLATION("Database constraint violation occurred", HttpStatus.CONFLICT),
    DATABASE_SYSTEM_ERROR("Database system error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("An unexpected database error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    TRANSACTION_ERROR("Transaction error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
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