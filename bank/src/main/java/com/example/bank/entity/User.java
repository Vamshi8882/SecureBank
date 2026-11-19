package com.example.bank.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(length = 20, unique = true)
    private String phone;

    @Column(length = 300)
    private String address;

    private LocalDate dateOfBirth;

    @Column(length = 500)
    private String profilePhotoUrl;

    @Column(nullable = false, length = 20)
    private String role = "CUSTOMER";

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
