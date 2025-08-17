package com.michelin.repository.wishlist;

import com.michelin.entity.wishlist.Wishlist;
import com.michelin.entity.wishlist.WishlistId;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Collection;

public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {

    Optional<Wishlist> findById(WishlistId id);

    @EntityGraph(attributePaths = {"restaurant"})
    List<Wishlist> findByUserId(Long userId);

    // ✅ userId + kakaoPlaceId 리스트로 한 번에 가져오기 (문자열 리스트 반환)
    @Query("select w.restaurant.kakaoPlaceId from Wishlist w " +
           "where w.user.id = :userId and w.restaurant.kakaoPlaceId in :kakaoPlaceIds")
    List<String> findKakaoPlaceIdsByUserIdAndRestaurant_KakaoPlaceIdIn(Long userId, Collection<String> kakaoPlaceIds);
    
    long countByKakaoPlaceId(String kakaoPlaceId);
}