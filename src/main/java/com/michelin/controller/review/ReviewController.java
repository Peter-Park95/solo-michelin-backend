package com.michelin.controller.review;

import com.michelin.dto.review.ReviewRequest;
import com.michelin.dto.review.ReviewResponse;
import com.michelin.service.review.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping
    public ReviewResponse createReview(@RequestBody @Valid ReviewRequest request){
        return reviewService.createReview(request);
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
            @RequestParam(defaultValue = "created") String orderBy, // 최신순 or 랭킹순
            @RequestParam(required = false) Double minRating // 4.0 이상 필터링
    ) {
        Page<ReviewResponse> reviewPage = reviewService.getReviewsByUserId(userId, page, size, orderBy, minRating);
        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviewPage.getContent());
        response.put("hasMore", reviewPage.hasNext()); // 다음 페이지 존재 여부

        return ResponseEntity.ok(response);
    }
}
