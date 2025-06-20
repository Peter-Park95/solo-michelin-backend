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

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.restaurant.id = :restaurantId AND r.deleted = 0")
    Float findAverageRatingByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.deleted = 0 AND r.imageUrl IS NOT NULL AND r.imageUrl <> ''")
    Page<Review> findWithImageByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM review WHERE rating >= 3.5 AND deleted = 0 ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Review> findRandomHighlightedReviews(@Param("limit") int limit);

}

