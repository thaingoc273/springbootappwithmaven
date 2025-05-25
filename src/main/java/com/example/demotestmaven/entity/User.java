package com.example.demotestmaven.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
// import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
// import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

@Data
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "VARCHAR(36)", nullable = false, updatable = false)
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at_local")
    private LocalDateTime createdAtLocal;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Role> roles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        //id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        // updatedAt = now;
        createdAtLocal = now.plusHours(2);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

} 