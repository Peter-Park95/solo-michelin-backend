package com.michelin.entity.user;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 512)
    private String profileImage;

    private String region;

    @Column(length = 100)
    private String introduction;

    private LocalDateTime created;

    private boolean deleted;

    protected void onCreate() {
        this.created = LocalDateTime.now();
    }
}
