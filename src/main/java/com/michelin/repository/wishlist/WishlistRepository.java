package com.michelin.repository.wishlist;

import com.michelin.entity.wishlist.Wishlist;
import com.michelin.entity.wishlist.WishlistId;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {
	
	Optional<Wishlist> findById(WishlistId id);
	
	@EntityGraph(attributePaths = {"restaurant"})
    List<Wishlist> findByUserId(Long userId);
    
}