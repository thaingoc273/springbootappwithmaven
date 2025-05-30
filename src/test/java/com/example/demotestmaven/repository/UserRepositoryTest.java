package com.example.demotestmaven.repository;

import com.example.demotestmaven.entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
//@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private String usernameTest = "testuser";
    private String passwordTest = "password123";
    private String emailTest = "test@example.com";

    private String nonexistentEmail = "nonexistent@example.com";
    private String nonexistentUsername = "nonexistentusername";

    private String usernameDuplicate = "duplicate";
    private String passwordDuplicate1 = "password456";
    private String passwordDuplicate2 = "password789";
    private String emailDuplicate1 = "user1@example.com";
    private String emailDuplicate2 = "user2@example.com";
    
    private LocalDateTime timeBefore = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    private LocalDateTime timeAfter = LocalDateTime.of(2025, 1, 2, 0, 0, 0);
   
    private LocalDateTime timeBeforeEmptyReturn = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
    private LocalDateTime timeAfterEmptyReturn = LocalDateTime.of(2020, 1, 2, 0, 0, 0);

    @BeforeEach
    void setUp() {
        // entityManager.clear();

        // Create a test user
        User user = new User();
        user.setUsername(usernameTest);
        user.setPassword(passwordTest);
        user.setEmail(emailTest);
        entityManager.persist(user);
        entityManager.flush();        
    }


    @Test
    public void testFindAll() {

        List<User> result = userRepository.findAll();
        // Verify the result
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(3); // 2 from data-test.sql + 1 from this test
    }   

    @Test
    public void testFindByUsername() {

        // Test the repository method
        Optional<User> found = userRepository.findByUsername(usernameTest);

        // Verify the result
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(usernameTest);
        assertThat(found.get().getEmail()).isEqualTo(emailTest);
    }

    @Test
    public void testExistsByUsername() {

        // Test the repository method
        boolean exists = userRepository.existsByUsername(usernameTest);
        boolean notExists = userRepository.existsByUsername(nonexistentUsername);

        // Verify the results
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    public void testExistsByEmail() {
       
        // Test the repository method
        boolean exists = userRepository.existsByEmail(emailTest);
        boolean notExists = userRepository.existsByEmail(nonexistentEmail);

        // Verify the results
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @Transactional
    void shouldEnforceUniqueUsername() {
        // Given
        User user1 = new User();
        user1.setUsername(usernameDuplicate);
        user1.setPassword(passwordDuplicate1);
        user1.setEmail(emailDuplicate1);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername(usernameDuplicate); // Same username
        user2.setPassword(passwordDuplicate2);
        user2.setEmail(emailDuplicate2);

        // When/Then
        try {
            userRepository.save(user2);
            entityManager.flush(); // Force the constraint check
            assertThat(false).isTrue(); // Should not reach here
        } catch (Exception e) {
            assertThat(e.getMessage().toLowerCase()).contains("unique");
        }
    }

    @Test
    @Transactional
    void findByCreatedAtBeforeAndAfter_ShouldReturnListOfUsers() {
        // Act: query for users created before a later date
        List<User> result = userRepository.findByCreatedAtBeforeAndAfter(timeBefore, timeAfter);

        // Assert
        assertThat(result.size()).isEqualTo(3); // 2 from data.sql + 1 from this test
    }

    @Test
    void findByCreatedAtBeforeAndAfter_WhenAfterIsBeforeBefore_ShouldReturnEmptyList() { 

        // Act
        List<User> result = userRepository.findByCreatedAtBeforeAndAfter(timeBeforeEmptyReturn, timeAfterEmptyReturn);

        // Assert
        assertThat(result).isEmpty();
    }   

} 