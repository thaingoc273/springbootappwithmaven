package com.example.demotestmaven.service;

import com.example.demotestmaven.entity.User;
import com.example.demotestmaven.exception.ApiErrorType;
import com.example.demotestmaven.exception.ApiException;
import com.example.demotestmaven.repository.UserRepository;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

@Service
public class AsyncUserService {

  @Autowired private UserRepository userRepository;

  private static final Logger logger = LoggerFactory.getLogger(AsyncUserService.class);

  @Async("threadPoolTaskExecutor")
  public void saveUsers(User user) {
    try {
      userRepository.save(user);
      logger.info(
          "Thread: {} and User: {} saved", Thread.currentThread().getName(), user.getUsername());
    } catch (DataAccessException e) {
      logger.error("Database error while saving user {}: {}", user.getUsername(), e.getMessage());

      if (e instanceof TransientDataAccessException) {
        // Handle temporary failures (connection issues, timeouts)
        if (e.getCause() instanceof JDBCConnectionException) {
          throw new ApiException(
              ApiErrorType.DATABASE_CONNECTION_ERROR, "Database connection error occurred");
        } else if (e.getCause() instanceof LockTimeoutException) {
          throw new ApiException(
              ApiErrorType.DATABASE_TIMEOUT_ERROR, "Database operation timed out");
        }
      } else if (e instanceof NonTransientDataAccessException) {
        // Handle permanent failures (constraint violations, etc.)
        if (e instanceof DataIntegrityViolationException) {
          throw new ApiException(
              ApiErrorType.DATABASE_CONSTRAINT_VIOLATION, "Database constraint violation occurred");
        }
      } else if (e instanceof JpaSystemException) {
        throw new ApiException(
            ApiErrorType.DATABASE_SYSTEM_ERROR, "Database system error occurred");
      }

      // Generic database error
      throw new ApiException(ApiErrorType.DATABASE_ERROR, "An unexpected database error occurred");
    } catch (TransactionSystemException e) {
      logger.error(
          "Transaction error while saving user {}: {}", user.getUsername(), e.getMessage());
      throw new ApiException(ApiErrorType.TRANSACTION_ERROR, "Transaction error occurred");
    }
  }
}
