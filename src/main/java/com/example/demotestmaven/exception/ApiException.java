package com.example.demotestmaven.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
  private final ApiErrorType errorType;
  private final transient Object[] args;

  public ApiException(ApiErrorType errorType) {
    super(errorType.getMessage());
    this.errorType = errorType;
    this.args = new Object[] {};
  }

  public ApiException(ApiErrorType errorType, Object... args) {
    super(errorType.getFormattedMessage(args));
    this.errorType = errorType;
    this.args = args;
  }

  public ApiException(ApiErrorType errorType, Throwable cause) {
    super(errorType.getMessage(), cause);
    this.errorType = errorType;
    this.args = new Object[] {};
  }

  public ApiException(ApiErrorType errorType, Throwable cause, Object... args) {
    super(errorType.getFormattedMessage(args), cause);
    this.errorType = errorType;
    this.args = args;
  }

  public ApiErrorType getErrorType() {
    return errorType;
  }

  public Object[] getArgs() {
    return args;
  }

  public HttpStatus getStatusCode() {
    return errorType.getStatus();
  }
}
