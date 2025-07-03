package com.michelin.entity.reviewlike;


import com.michelin.entity.review.Review;
import com.michelin.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@IdClass(ReviewLikeId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLike {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Review review;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime created;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private int deleted;

}
