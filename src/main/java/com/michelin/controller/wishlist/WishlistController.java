package com.michelin.controller.wishlist;

import com.michelin.dto.wishlist.WishlistResponse;
import com.michelin.service.wishlist.WishlistService;
import com.michelin.util.JwtUtil;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final JwtUtil jwtUtil;

    @PostMapping("/{kakaoPlaceId}")
    public ResponseEntity<?> toggleWishlist(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable String kakaoPlaceId
    ) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token); // JWT에서 userId 추출

        wishlistService.toggleWishlist(userId, kakaoPlaceId);
        return ResponseEntity.ok("위시리스트 토글 완료");
    }
    
    @GetMapping
    public ResponseEntity<?> getMyWishlist(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        List<WishlistResponse> wishlist = wishlistService.getWishlistByUserId(userId);
        return ResponseEntity.ok(wishlist);
    }
}
