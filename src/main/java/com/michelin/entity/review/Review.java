package com.michelin.entity.review;
import com.michelin.entity.restaurant.Restaurant;
import com.michelin.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private float rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;


    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime created;

    @Column
    private LocalDateTime modified;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private int deleted;

}
