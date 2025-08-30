package com.michelin.dto.review;

import com.michelin.entity.review.Review;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewSummaryResponse {
	private Long id;
    private String restaurantName;
    private String restaurantAddress;
    private float rating;
    private String comment;
    private String imageUrl;
    private String userName;

    //review like counting 관련 추가
    private Long likeCount;
    private Boolean likedByMe;

    public static ReviewSummaryResponse from(Review review) {
        ReviewSummaryResponse dto = new ReviewSummaryResponse();
        dto.setId(review.getId());
        dto.setRestaurantName(review.getRestaurant().getName());
        dto.setRestaurantAddress(review.getRestaurant().getAddress());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setImageUrl(review.getImageUrl());
        dto.setUserName(review.getUser().getUsername());
        dto.setLikeCount(0L); // 기본값
        dto.setLikedByMe(false); // 기본값
        return dto;
    }

    public static ReviewSummaryResponse from(Review review, Long likeCount, boolean likedByMe) {
        ReviewSummaryResponse dto = from(review); // 위에 만든 기본 from() 재사용
        dto.setLikeCount(likeCount);
        dto.setLikedByMe(likedByMe);
        return dto;
    }
}
