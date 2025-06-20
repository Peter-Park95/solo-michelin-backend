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

    Page<ReviewResponse> getReviewsByUserId(Long userId, int page, int size, String orderBy, Double minRating);

    Page<ReviewResponse> getReviewsWithImageByUserId(Long userId, int page, int size);

    public ReviewResponse createReviewWithKakaoPlace(ReviewWithKakaoRequest request, MultipartFile image, Long userId);

    public List<ReviewSummaryResponse> getHighlightedReviews(int limit);
}
