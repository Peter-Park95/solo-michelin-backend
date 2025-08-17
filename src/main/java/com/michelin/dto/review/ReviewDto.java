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
	private String userName;
    private String comment;
    private int rating;
}