package com.michelin.entity.wishlist;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WishlistId implements Serializable {
	
    private Long userId;
    
    private String kakaoPlaceId;
    
}