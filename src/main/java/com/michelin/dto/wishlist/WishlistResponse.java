package com.michelin.dto.wishlist;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WishlistResponse {
	
    private String kakaoPlaceId;
    
    private String restaurantName;
    
    private String imageUrl;

    private String category;
    
    private String address;
    
}
