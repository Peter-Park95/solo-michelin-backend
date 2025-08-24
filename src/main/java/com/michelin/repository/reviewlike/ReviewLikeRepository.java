package com.michelin.repository.reviewlike;

import com.michelin.entity.reviewlike.ReviewLike;
import com.michelin.entity.reviewlike.ReviewLikeId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {
	
//    Optional<ReviewLike> findById(ReviewLikeId id);

//    @EntityGraph(attributePaths = {"review"})
//    List<ReviewLike> findByUserId(Long userId);
    
    // 특정 유저가 특정 리뷰에 좋아요 눌렀는지 확인
    Optional<ReviewLike> findByUserIdAndReviewIdAndDeleted(Long userId, Long reviewId, int deleted);

//    // 특정 리뷰의 좋아요 개수
//    long countByReviewId(Long reviewId);

//    // 특정 리뷰의 모든 좋아요 가져오기 (필요 시)
//    List<ReviewLike> findByReviewId(Long reviewId);

//    // 특정 리뷰 + 특정 유저의 좋아요 삭제
//    void deleteByUserIdAndReviewId(Long userId, Long reviewId);
    
//    Optional<ReviewLike> findByIdAndDeleted(ReviewLikeId id, int deleted);

    @EntityGraph(attributePaths = {"review"})
    List<ReviewLike> findByUserIdAndDeleted(Long userId, int deleted);

    long countByReviewIdAndDeleted(Long reviewId, int deleted);
}

