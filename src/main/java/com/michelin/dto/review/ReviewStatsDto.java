package com.michelin.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStatsDto {
    //검색 시 마크 인포윈도우에 전체 리뷰 수 조회
    private long reviewCount;
    private long wishlistCount;
}