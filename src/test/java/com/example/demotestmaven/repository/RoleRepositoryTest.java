package com.example.demotestmaven.repository;

import com.example.demotestmaven.entity.Role;
import com.example.demotestmaven.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
//@Sql(scripts = "/data-test.sql")//, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testFindByUser_Username() {
        // Create a test user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        entityManager.persist(user);

        // Create roles for the user
        Role adminRole = new Role();
        adminRole.setUser(user);
        adminRole.setRolecode("ADMIN");
        adminRole.setRoletype("SYSTEM_ADMIN");
        entityManager.persist(adminRole);

        Role userRole = new Role();
        userRole.setUser(user);
        userRole.setRolecode("USER");
        userRole.setRoletype("REGULAR_USER");
        entityManager.persist(userRole);

        entityManager.flush();
        entityManager.clear();

        // Test the repository method
        List<Role> roles = roleRepository.findByUser_Username("testuser");

        // Verify the results
        assertThat(roles).hasSize(2);
        assertThat(roles).extracting("rolecode")
                .containsExactlyInAnyOrder("ADMIN", "USER");
    }

    @Test
    public void testExistsByUser_UsernameAndRolecode() {
        // Create a test user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        entityManager.persist(user);

        // Create a role for the user
        Role role = new Role();
        role.setUser(user);
        role.setRolecode("ADMIN");
        role.setRoletype("SYSTEM_ADMIN");
        entityManager.persist(role);

        entityManager.flush();
        entityManager.clear();

        // Test the repository method
        boolean exists = roleRepository.existsByUser_UsernameAndRolecode("testuser", "ADMIN");
        boolean notExists = roleRepository.existsByUser_UsernameAndRolecode("testuser", "MANAGER");

        // Verify the results
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
} 