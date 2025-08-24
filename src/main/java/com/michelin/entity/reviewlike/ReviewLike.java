package com.michelin.entity.reviewlike;


import com.michelin.entity.review.Review;
import com.michelin.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLike {

	@EmbeddedId
    private ReviewLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("reviewId")
    @JoinColumn(name = "review_id")
    private Review review;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime created;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private int deleted;

    // helper method
    public boolean isActive() {
        return this.deleted == 0;
    }

    public void activate() {
        this.deleted = 0;
    }

    public void deactivate() {
        this.deleted = 1;
    }

}
