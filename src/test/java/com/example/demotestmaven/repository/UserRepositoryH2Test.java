package com.example.demotestmaven.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demotestmaven.entity.Role;
import com.example.demotestmaven.entity.User;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
// @TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
// @Sql(scripts = {"/data-test.sql"})
@Transactional
public class UserRepositoryH2Test {

  @Autowired private UserRepository userRepository;

  @Autowired private RoleRepository roleRepository;

  @Autowired private EntityManager entityManager;

  @Test
  void contextLoads() {
    assertThat(userRepository).isNotNull();
  }

  @Test
  void shouldFindUserByUsername() {
    // Given
    String username = "testuser1";

    // When
    Optional<User> foundUser = userRepository.findByUsername(username);

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo(username);
    assertThat(foundUser.get().getEmail()).isEqualTo("test1@example.com");
  }

  @Test
  @Transactional
  void shouldFindAllUsers() {
    // When
    List<User> users = userRepository.findAll();

    // Then
    assertThat(users).isNotEmpty();
    assertThat(users.size()).isEqualTo(2);
  }

  @Test
  @Transactional
  void shouldInsertAndRetrieveUser() {
    // Given
    User newUser = new User();
    newUser.setUsername("newuser");
    newUser.setPassword("password123");
    newUser.setEmail("newuser@example.com");

    // When
    User savedUser = userRepository.save(newUser);
    Optional<User> foundUser = userRepository.findByUsername("newuser");

    // Then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo("newuser");
    assertThat(foundUser.get().getEmail()).isEqualTo("newuser@example.com");
  }

  @Test
  @Transactional
  void shouldUpdateUser() {
    // Given
    User user = userRepository.findByUsername("testuser1").orElseThrow();
    String newEmail = "updated@example.com";

    // When
    user.setEmail(newEmail);
    userRepository.save(user);
    User updatedUser = userRepository.findByUsername("testuser1").orElseThrow();

    // Then
    assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
  }

  @Test
  @Transactional
  void shouldDeleteUser() {
    // Given
    User user = userRepository.findByUsername("testuser1").orElseThrow();

    // When
    userRepository.delete(user);
    Optional<User> deletedUser = userRepository.findByUsername("testuser1");

    // Then
    assertThat(deletedUser).isEmpty();
  }

  @Test
  @Transactional
  void shouldHandleUserWithRoles() {
    // Given
    User user = new User();
    user.setUsername("userwithrole");
    user.setPassword("password123");
    user.setEmail("userwithrole@example.com");
    userRepository.save(user);

    Role role = new Role();
    role.setUser(user);
    role.setRolecode("ADMIN");
    role.setRoletype("SYSTEM");
    roleRepository.save(role);

    // When
    User foundUser = userRepository.findByUsername("userwithrole").orElseThrow();
    List<Role> userRoles = roleRepository.findByUser_Username("userwithrole");

    // Then
    assertThat(foundUser).isNotNull();
    assertThat(userRoles).hasSize(1);
    assertThat(userRoles.get(0).getRolecode()).isEqualTo("ADMIN");
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
  void shouldEnforceForeignKeyConstraint() {
    // Given
    Role role = new Role();
    User nonExistentUser = new User();
    nonExistentUser.setUsername("nonexistentuser");
    role.setUser(nonExistentUser);
    role.setRolecode("ADMIN");
    role.setRoletype("SYSTEM");

    // When/Then
    try {
      roleRepository.save(role);
      entityManager.flush(); // Force the constraint check
      assertThat(false).isTrue(); // Should not reach here
    } catch (Exception e) {
      assertThat(e.getMessage()).contains("transient value");
    }
  }
}
