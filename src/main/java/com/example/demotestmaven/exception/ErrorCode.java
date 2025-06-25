package com.example.demotestmaven.exception;

public enum ErrorCode {
  // Authentication & Authorization
  UNAUTHORIZED_ACCESS("You don't have permission to access this resource"),
  FORBIDDEN_OPERATION("You don't have permission to perform this operation"),

  // Resource errors
  RESOURCE_NOT_FOUND("Resource not found"),
  USER_NOT_FOUND("User not found with username: %s"),
  DUPLICATE_RESOURCE("Resource already exists"),

  // Validation errors
  INVALID_INPUT("Invalid input provided"),
  INVALID_USERNAME("Username is required"),
  INVALID_PASSWORD("Password must be at least 8 characters long"),
  INVALID_EMAIL("Invalid email format"),
  INVALID_DATE_FORMAT("Invalid date time format, please use yyyy-MM-dd"),
  INVALID_ROLE("Invalid role provided"),

  // Business logic errors
  OPERATION_FAILED("Operation failed"),
  DATA_INTEGRITY_VIOLATION("Data integrity violation occurred");

  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public String formatMessage(Object... args) {
    return String.format(message, args);
  }
}
