package com.michelin.dto.review;

import com.michelin.entity.review.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {
	private Long reviewId;
    private Long id;
    private Long userId;
    private Long restaurantId;
    private String restaurantName;
    private String restaurantImageUrl;
    private float foodRating;
    private float moodRating;
    private float serviceRating;
    private float restaurantAvgRating;
    private float rating;
    private String comment;
    private String reviewImageUrl;
    private LocalDateTime created;
    private LocalDateTime modified;

    public static ReviewResponse from(Review review, boolean liked, long likeCount){
        return ReviewResponse.builder()
        		.reviewId(review.getId())
                .id(review.getId())
                .userId(review.getUser().getId())
                .restaurantId(review.getRestaurant().getId())
                .restaurantName(review.getRestaurant().getName())
                .restaurantImageUrl(review.getRestaurant().getImageUrl())
                .restaurantAvgRating(review.getRestaurant().getAvgRating())
                .foodRating(review.getFoodRating())
                .moodRating(review.getMoodRating())
                .serviceRating(review.getServiceRating())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewImageUrl(review.getImageUrl())
                .created(review.getCreated())
                .modified(review.getModified())
                .build();
    }
    
    public static ReviewResponse from(Review review) {
        return from(review, false, 0); // 기본값
    }
}
