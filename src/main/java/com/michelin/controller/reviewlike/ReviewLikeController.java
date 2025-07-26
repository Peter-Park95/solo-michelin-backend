package com.michelin.controller.reviewlike;

import com.michelin.entity.reviewlike.ReviewLike;
import com.michelin.service.reviewlike.ReviewLikeService;
import com.michelin.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review_like")
@RequiredArgsConstructor
public class ReviewLikeController {
    private final ReviewLikeService reviewLikeService;
    private final JwtUtil jwtUtil;

    @PostMapping("/{reviewId}")
    public ResponseEntity<?> toggleReviewLike(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long reviewId
    ) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        reviewLikeService.toggleReviewLike(userId, reviewId);
        return ResponseEntity.ok("좋아요 토글 완료");
    }
}

