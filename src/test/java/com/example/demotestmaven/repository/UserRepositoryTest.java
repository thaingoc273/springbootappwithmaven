package com.example.demotestmaven.repository;

import com.example.demotestmaven.entity.User;
import com.example.demotestmaven.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(TestConfig.class)
@Transactional
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindAll() {
        // Create a test user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // Test the repository method
        List<User> result = userRepository.findAll();

        // Verify the result
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(3);        
    }   

    

    @Test
    public void testFindByUsername() {
        // Create a test user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // Test the repository method
        Optional<User> found = userRepository.findByUsername("testuser");

        // Verify the result
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void testExistsByUsername() {
        // Create a test user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // Test the repository method
        boolean exists = userRepository.existsByUsername("testuser");
        boolean notExists = userRepository.existsByUsername("nonexistent");

        // Verify the results
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    public void testExistsByEmail() {
        // Create a test user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // Test the repository method
        boolean exists = userRepository.existsByEmail("test@example.com");
        boolean notExists = userRepository.existsByEmail("nonexistent@example.com");

        // Verify the results
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @Transactional
    void shouldEnforceUniqueUsername() {
        // Given
        User user1 = new User();
        user1.setUsername("duplicate");
        user1.setPassword("password123");
        user1.setEmail("user1@example.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("duplicate"); // Same username
        user2.setPassword("password456");
        user2.setEmail("user2@example.com");

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
    void findByCreatedAtBefore_ShouldReturnListOfUsers() {
        // Arrange
        User user1 = new User();
        user1.setUsername("testuser3");
        user1.setPassword("password123");
        user1.setEmail("user3@example.com");    
        // Set createdAt to a fixed date
        user1.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        user1.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));

        entityManager.persist(user1);
        entityManager.flush();

        // Act: query for users created before a later date
        List<User> result = userRepository.findByCreatedAtBefore(LocalDateTime.of(2026, 1, 2, 0, 0, 0));

        // Assert
        assertTrue(result.size() == 3);        
    }

    @Test
    void findByCreatedAtBefore_ShouldReturnEmptyList() {
        // Arrange
        User user1 = new User();
        user1.setUsername("testuser4");
        user1.setPassword("password123");
        user1.setEmail("user4@example.com");
        user1.setCreatedAt(LocalDateTime.of(2025, 2, 20, 1, 0, 0));
        user1.setUpdatedAt(LocalDateTime.of(2025, 2, 20, 1, 0, 0));

        entityManager.persist(user1);
        entityManager.flush();      

        // Act
        List<User> result = userRepository.findByCreatedAtBefore(LocalDateTime.of(2025, 2, 10, 1, 0, 0));

        // Assert
        assertThat(result).isEmpty();
    }  

} 