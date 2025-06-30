package com.michelin.service.wishlist;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.michelin.dto.wishlist.WishlistResponse;
import com.michelin.entity.restaurant.Restaurant;
import com.michelin.entity.user.User;
import com.michelin.entity.wishlist.Wishlist;
import com.michelin.entity.wishlist.WishlistId;
import com.michelin.repository.restaurant.RestaurantRepository;
import com.michelin.repository.user.UserRepository;
import com.michelin.repository.wishlist.WishlistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public void toggleWishlist(Long userId, String kakaoPlaceId) {
    	WishlistId id = new WishlistId(userId, kakaoPlaceId);
        Optional<Wishlist> existing = wishlistRepository.findById(id);

        if (existing.isPresent()) {
            wishlistRepository.deleteById(id); // 있으면 삭제
        } else {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

            Restaurant restaurant = restaurantRepository.findByKakaoPlaceId(kakaoPlaceId)
                .orElseThrow(() -> new RuntimeException("해당 식당 없음"));

            Wishlist wishlist = Wishlist.builder()
            	    .userId(userId)  // 필수 값
            	    .kakaoPlaceId(kakaoPlaceId)  // 필수 값
            	    .user(user)  // 연관관계 설정
            	    .restaurant(restaurant)
            	    .created(LocalDateTime.now())
            	    .build();

            wishlistRepository.save(wishlist);
        }
    }
    
    @Override
    public List<WishlistResponse> getWishlistByUserId(Long userId) {
    	List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        return wishlists.stream()
            .filter(w -> w.getRestaurant() != null)  // restaurant가 null인 경우 제외
            .map(w -> {
                var r = w.getRestaurant();
                return WishlistResponse.builder()
                    .kakaoPlaceId(w.getKakaoPlaceId())
                    .restaurantName(r != null ? r.getName() : "알 수 없음")
                    .imageUrl(r != null ? r.getImageUrl() : null)
                    .category(r != null ? r.getCategory() : null)
                    .address(r != null ? r.getAddress() : null)
                    .build();
            })
            .collect(Collectors.toList());
    }
}