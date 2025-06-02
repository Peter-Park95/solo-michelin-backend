package com.michelin.dto.review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewUpdateRequest {

    private Float foodRating;
    private Float moodRating;
    private Float serviceRating;
    private Float rating; // 평균 평점
    private String comment;
}