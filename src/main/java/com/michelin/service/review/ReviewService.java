package com.michelin.service.review;

import com.michelin.dto.review.ReviewRequest;
import com.michelin.dto.review.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request, MultipartFile image); // ✅ 이미지 추가된 버전
    List<ReviewResponse> getAllReviews();
    ReviewResponse getReviewById(Long id);
    ReviewResponse updateReview(Long id, ReviewRequest request);
    void deleteReview(Long id);
    Page<ReviewResponse> getReviewsByUserId(Long userId, int page, int size, String orderBy, Double minRating);
}
