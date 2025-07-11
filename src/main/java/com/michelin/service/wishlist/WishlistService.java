package com.michelin.service.wishlist;

import java.util.List;

import com.michelin.dto.wishlist.WishlistResponse;

public interface WishlistService {
	
    void toggleWishlist(Long userId, String kakaoPlaceId);
    
    List<WishlistResponse> getWishlistByUserId(Long userId);
    
}