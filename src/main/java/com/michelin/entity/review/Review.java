package com.michelin.entity.review;
import com.michelin.entity.restaurant.Restaurant;
import com.michelin.entity.reviewlike.ReviewLike;
import com.michelin.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(name = "food_rating")
    private float foodRating;
    @Column(name = "mood_rating")
    private float moodRating;
    @Column(name = "service_rating")
    private float serviceRating;
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

    // 좋아요 목록 (양방향 매핑)
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLike> reviewLikes = new ArrayList<>();
}
