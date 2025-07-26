package com.michelin.dto.review;

import com.michelin.entity.review.Review;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewSummaryResponse {
    private String restaurantName;
    private String restaurantAddress;
    private float rating;
    private String comment;
    private String imageUrl;
    private String userName;

    public static ReviewSummaryResponse from(Review review) {
        ReviewSummaryResponse dto = new ReviewSummaryResponse();
        dto.setRestaurantName(review.getRestaurant().getName());
        dto.setRestaurantAddress(review.getRestaurant().getAddress());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setImageUrl(review.getImageUrl()); // 리뷰에 이미지 필드가 있어야 함
        dto.setUserName(review.getUser().getUsername());
        return dto;
    }
}
