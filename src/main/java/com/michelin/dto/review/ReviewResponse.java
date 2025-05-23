package com.michelin.dto.review;

import com.michelin.entity.review.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {
    private Long id;
    private Long userId;
    private Long restaurantId;
    private String restaurantName;
    private String restaurantImageUrl;
    private float restaurantAvgRating;
    private float rating;
    private String comment;
    private String reviewImageUrl;
    private LocalDateTime created;
    private LocalDateTime modified;

    public static ReviewResponse from(Review review){
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .restaurantId(review.getRestaurant().getId())
                .restaurantName(review.getRestaurant().getName())
                .restaurantImageUrl(review.getRestaurant().getImageUrl())
                .restaurantAvgRating(review.getRestaurant().getAvgRating())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewImageUrl(review.getImageUrl())
                .created(review.getCreated())
                .modified(review.getModified())
                .build();
    }
}
