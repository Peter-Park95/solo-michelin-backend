package com.michelin.service.reviewlike;

import com.michelin.entity.restaurant.Restaurant;
import com.michelin.entity.review.Review;
import com.michelin.entity.reviewlike.ReviewLike;
import com.michelin.entity.reviewlike.ReviewLikeId;

import com.michelin.entity.user.User;
import com.michelin.entity.wishlist.Wishlist;
import com.michelin.repository.review.ReviewRepository;
import com.michelin.repository.reviewlike.ReviewLikeRepository;
import com.michelin.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewLikeServiceImpl  implements  ReviewLikeService{

    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public void toggleReviewLike(Long userId, Long reviewId){
        ReviewLikeId id = new ReviewLikeId(userId, reviewId);
        Optional<ReviewLike> existing = reviewLikeRepository.findById(id);

        if (existing.isPresent()) {
            reviewLikeRepository.deleteById(id); // 있으면 삭제
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자 없음"));

            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new RuntimeException("해당 리뷰 없음"));

            ReviewLike reviewLike = ReviewLike.builder()
                    .userId(userId)  // 필수 값
                    .reviewId(reviewId)  // 필수 값
                    .user(user)  // 연관관계 설정
                    .review(review)
                    .created(LocalDateTime.now())
                    .build();

            reviewLikeRepository.save(reviewLike);
        }
    }
}
