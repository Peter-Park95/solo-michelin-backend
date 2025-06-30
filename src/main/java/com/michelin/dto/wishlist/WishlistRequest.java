package com.michelin.dto.wishlist;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistRequest {
	
    private String user_id;

    private String kakao_place_id;


}
