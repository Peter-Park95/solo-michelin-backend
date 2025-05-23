package com.michelin.dto.review;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ReviewRequest {

    @NotNull(message = "유저 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "음식점 ID는 필수입니다.")
    private Long restaurantId;


    @Min(value = 0)
    @Max(value = 5)
    private float rating;

    @NotBlank(message = "한줄평은 필수입니다.")
    private String comment;
    private String imageUrl;
}
