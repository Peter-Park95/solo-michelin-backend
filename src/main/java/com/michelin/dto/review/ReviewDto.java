package com.michelin.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    //팝업 전체 리뷰 조회
	private Long reviewId;
	private String userName;
    private String comment;
    private float rating;
    private String imageUrl;
    private float foodRating;
    private float moodRating;
    private float serviceRating;
    private Long likeCount;   // 좋아요 개수
    private boolean liked;    // 현재 로그인 유저가 눌렀는지 여부
}