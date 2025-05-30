package com.michelin.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewWithKakaoRequest {

    @JsonProperty("kakaoPlaceId")
    private String kakaoPlaceId;

    @JsonProperty("restaurantName")
    private String restaurantName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("mapUrl")
    private String mapUrl;

    @JsonProperty("category")
    private String category;

    @JsonProperty("rating")
    private float rating;

    @JsonProperty("comment")
    private String comment;
}
