package com.pospick.pospick.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity @Table(name = "users")
@Getter @Setter
@NoArgsConstructor

public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String loginId;
    private String password;
    private String name;
    private String email;
    private String role; // ORGANIZER, SELLER
    private LocalDateTime createdAt = LocalDateTime.now();
}