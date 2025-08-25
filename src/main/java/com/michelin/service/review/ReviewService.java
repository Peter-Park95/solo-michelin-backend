package com.michelin.service.review;

import com.michelin.dto.review.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewAddRequest request, MultipartFile image); // ✅ 이미지 추가된 버전

    List<ReviewResponse> getAllReviews();

    ReviewResponse getReviewById(Long id);

    ReviewResponse updateReview(Long id, ReviewUpdateRequest request, MultipartFile imageFile);

    void deleteReview(Long id);

    Page<ReviewResponse> getReviewsByUserId(Long userId, int page, int size, String orderBy, Double minRating, Boolean withImage, String search);

    Page<ReviewResponse> getReviewsWithImageByUserId(Long userId, int page, int size);

    public ReviewResponse createReviewWithKakaoPlace(ReviewWithKakaoRequest request, MultipartFile image, Long userId);
    
    void deleteReviewImage(Long reviewId);

    public List<ReviewSummaryResponse> getHighlightedReviews(int limit);

    //검색 시 마크 인포윈도우에 전체 리뷰 수 조회
    ReviewStatsDto getReviewStats(String kakaoPlaceId);
    
    List<ReviewDto> getReviewsByPlace(String kakaoPlaceId);

    boolean toggleReviewLike(Long reviewId, Long userId);
    
    long getReviewLikeCount(Long reviewId);
    
    boolean hasUserLikedReview(Long reviewId, Long userId);
    
    Page<ReviewResponse> getReviewsByUserAndSearch(Long userId, String search, int page, int size, String orderBy, Double minRating, String category);
}
