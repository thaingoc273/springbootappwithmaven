package com.example.demotestmaven.repository;

import com.example.demotestmaven.entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private String usernameTest = "testuser";
    private String passwordTest = "password123";
    private String emailTest = "test@example.com";

    private String nonexistentEmail = "nonexistent@example.com";
    private String nonexistentUsername = "nonexistent";

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
        // Create a test user
        User user = new User();
        user.setUsername(usernameTest);
        user.setPassword(passwordTest);
        user.setEmail(emailTest);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setCreatedAtZone(ZonedDateTime.now(ZoneId.systemDefault()));
        user.setUpdatedAt(now);
        entityManager.persist(user);
        entityManager.flush();        
    }

    @Test
    public void testFindAll() {
        List<User> result = userRepository.findAll();
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(3); // 4 from V3__Insert_initial_data.sql + 1 from setUp
    }   

    @Test
    public void testFindByUsername() {
        Optional<User> found = userRepository.findByUsername(usernameTest);
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(usernameTest);
        assertThat(found.get().getEmail()).isEqualTo(emailTest);
    }

    @Test
    public void testExistsByUsername() {
        boolean exists = userRepository.existsByUsername(usernameTest);
        boolean notExists = userRepository.existsByUsername(nonexistentUsername);
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    public void testExistsByEmail() {
        boolean exists = userRepository.existsByEmail(emailTest);
        boolean notExists = userRepository.existsByEmail(nonexistentEmail);
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @Transactional
    void shouldEnforceUniqueUsername() {
        User user1 = new User();
        user1.setUsername(usernameDuplicate);
        user1.setPassword(passwordDuplicate1);
        user1.setEmail(emailDuplicate1);
        LocalDateTime now = LocalDateTime.now();
        user1.setCreatedAt(now);
        user1.setCreatedAtZone(ZonedDateTime.now(ZoneId.systemDefault()));
        user1.setUpdatedAt(now);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername(usernameDuplicate); // Same username
        user2.setPassword(passwordDuplicate2);
        user2.setEmail(emailDuplicate2);
        user2.setCreatedAt(now);
        user2.setCreatedAtZone(ZonedDateTime.now(ZoneId.systemDefault()));
        user2.setUpdatedAt(now);

        try {
            userRepository.save(user2);
            entityManager.flush();
            assertThat(false).isTrue(); // Should not reach here
        } catch (Exception e) {
            assertThat(e.getMessage().toLowerCase()).contains("unique");
        }
    }

    @Test
    @Transactional
    void findByCreatedAtBeforeAndAfter_ShouldReturnListOfUsers() {
        List<User> result = userRepository.findByCreatedAtBeforeAndAfter(timeBefore, timeAfter);
        assertThat(result.size()).isGreaterThan(0);
    }

    @Test
    void findByCreatedAtBeforeAndAfter_WhenAfterIsBeforeBefore_ShouldReturnEmptyList() { 
        List<User> result = userRepository.findByCreatedAtBeforeAndAfter(timeBeforeEmptyReturn, timeAfterEmptyReturn);
        assertThat(result).isEmpty();
    }  
} 