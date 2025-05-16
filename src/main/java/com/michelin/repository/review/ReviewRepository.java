package com.michelin.repository.review;

import com.michelin.entity.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.deleted = 0")
    Page<Review> findByUserIdWithSort(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.deleted = 0 AND r.rating >= :rating")
    Page<Review> findByUserIdWithMinRating(@Param("userId") Long userId, @Param("rating") Double rating, Pageable pageable);
}

