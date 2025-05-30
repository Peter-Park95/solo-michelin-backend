package com.michelin.controller.review;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michelin.dto.review.ReviewRequest;
import com.michelin.dto.review.ReviewResponse;
import com.michelin.dto.review.ReviewWithKakaoRequest;
import com.michelin.service.review.ReviewService;
import com.michelin.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @PostMapping(value = "/kakao", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
}

