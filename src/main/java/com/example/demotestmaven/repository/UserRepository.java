package com.example.demotestmaven.repository;

import com.example.demotestmaven.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {
  @EntityGraph(attributePaths = "roles")
  Optional<User> findByUsername(String username);

  @EntityGraph(attributePaths = "roles")
  Optional<User> findByEmail(String email);

  @EntityGraph(attributePaths = "roles")
  List<User> findAll();

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
  List<User> findAllWithRoles();

  @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
  Optional<User> findByUsernameWithRoles(@Param("username") String username);

  @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
  Optional<User> findByEmailWithRoles(@Param("email") String email);

  @Query(
      "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.createdAt <= :dateTimeBefore AND u.createdAt >= :dateTimeAfter")
  List<User> findByCreatedAtBeforeAndAfter(
      @Param("dateTimeBefore") LocalDateTime dateTimeBefore,
      @Param("dateTimeAfter") LocalDateTime dateTimeAfter);
}
