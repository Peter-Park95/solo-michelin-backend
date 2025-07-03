package com.michelin.repository.reviewlike;

import com.michelin.entity.reviewlike.ReviewLike;
import com.michelin.entity.reviewlike.ReviewLikeId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {
    Optional<ReviewLike> findById(ReviewLikeId id);

    @EntityGraph(attributePaths = {"review"})
    List<ReviewLike> findByUserId(Long userId);
}

