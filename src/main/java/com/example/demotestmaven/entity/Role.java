package com.example.demotestmaven.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

@Data
@Entity
@Table(name = "role")
public class Role {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "VARCHAR(36)", nullable = false)
    private String id; 

    @Column(nullable = false, length = 50)
    private String rolecode;

    @Column(nullable = false, length = 50)
    private String roletype;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at_local")
    private LocalDateTime createdAtLocal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        // id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        // updatedAt = now;
        createdAtLocal = now.plusHours(2);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 