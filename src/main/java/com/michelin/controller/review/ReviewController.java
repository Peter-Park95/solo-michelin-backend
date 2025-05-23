package com.michelin.controller.review;

import com.michelin.dto.review.ReviewRequest;
import com.michelin.dto.review.ReviewResponse;
import com.michelin.service.review.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService){
        this.reviewService = reviewService;
    }

    // ✅ 이미지 파일도 함께 받도록 Multipart 방식으로 변경
    // ✅ image는 @RequestPart(required = false)로 nullable 처리됨
    @PostMapping(consumes = "multipart/form-data")
    public ReviewResponse createReview(
            @RequestPart("request") @Valid ReviewRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ){
        return reviewService.createReview(request, image);
    }

    @GetMapping
    public List<ReviewResponse> getAllReviews(){
        return reviewService.getAllReviews();
    }

    @GetMapping("/{id}")
    public ReviewResponse getReviewById(@PathVariable Long id){
        return reviewService.getReviewById(id);
    }

    @PutMapping("/{id}")
    public ReviewResponse updateReview(@PathVariable Long id, @RequestBody @Valid ReviewRequest request){
        return reviewService.updateReview(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id){
        reviewService.deleteReview(id);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getReviewsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "created") String orderBy,
            @RequestParam(required = false) Double minRating
    ) {
        Page<ReviewResponse> reviewPage = reviewService.getReviewsByUserId(userId, page, size, orderBy, minRating);
        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviewPage.getContent());
        response.put("hasMore", reviewPage.hasNext());
        response.put("totalCount", reviewPage.getTotalElements());

        return ResponseEntity.ok(response);
    }
}

// ReviewService 인터페이스에도 메서드 시그니처 추가 필요:
// ReviewResponse createReview(ReviewRequest request, MultipartFile image);