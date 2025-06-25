package com.michelin.controller.review;

import com.michelin.dto.review.ReviewAddRequest;
import com.michelin.dto.review.ReviewResponse;
import com.michelin.dto.review.ReviewUpdateRequest;
import com.michelin.dto.review.ReviewWithKakaoRequest;
import com.michelin.service.review.ReviewService;
import com.michelin.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final JwtUtil jwtUtil;

    public ReviewController(ReviewService reviewService, JwtUtil jwtUtil){
        this.reviewService = reviewService;
        this.jwtUtil = jwtUtil;
    }

    // ✅ 이미지 파일도 함께 받도록 Multipart 방식으로 변경
    // ✅ image는 @RequestPart(required = false)로 nullable 처리됨
    @PostMapping(consumes = "multipart/form-data")
    public ReviewResponse createReview(
            @RequestPart("request") @Valid ReviewAddRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ){
        return reviewService.createReview(request, image);
    }

    @GetMapping
    public List<ReviewResponse> getAllReviews(){
        return reviewService.getAllReviews();
    }

    @GetMapping("/{id}") // 사용중 : 리뷰수정페이지
    public ReviewResponse getReviewById(@PathVariable Long id){
        return reviewService.getReviewById(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ReviewResponse updateReview(
            @PathVariable Long id,
            @RequestPart("review") @Valid ReviewUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        return reviewService.updateReview(id, request, imageFile);
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
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Boolean withImage
    ) {
        Page<ReviewResponse> reviewPage = reviewService.getReviewsByUserId(userId, page, size, orderBy, minRating, withImage);
        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviewPage.getContent());
        response.put("hasMore", reviewPage.hasNext());
        response.put("totalCount", reviewPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/kakao", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 사용중
    public ResponseEntity<ReviewResponse> createReviewWithKakao(
            @RequestPart("review") ReviewWithKakaoRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest httpRequest // ✅ 인증 정보 직접 추출용
    ) {
        String token = extractJwtFromRequest(httpRequest);
        if (token == null || !jwtUtil.validateToken(token)) {
            throw new RuntimeException("인증 실패: JWT 토큰이 유효하지 않거나 누락되었습니다.");
        }

        Long userId = jwtUtil.getUserIdFromToken(token); // ✅ userId 직접 추출
        ReviewResponse response = reviewService.createReviewWithKakaoPlace(request, image, userId);
        return ResponseEntity.ok(response);
    }

    // ✅ 토큰 추출 유틸 메서드
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    @PatchMapping("/{id}/image")
    public ResponseEntity<?> deleteReviewImage(@PathVariable Long id) {
        try {
            reviewService.deleteReviewImage(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 삭제 실패");
        }
    }
}

