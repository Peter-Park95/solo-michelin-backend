package com.michelin.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Min(value = 0)
    @Max(value = 5)
    private int foodRating;

    @Min(value = 0)
    @Max(value = 5)
    private int moodRating;

    @Min(value = 0)
    @Max(value = 5)
    private int serviceRating;

    @JsonProperty("comment")
    private String comment;
}
