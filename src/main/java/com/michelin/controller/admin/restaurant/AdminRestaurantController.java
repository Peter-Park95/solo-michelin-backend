package com.michelin.controller.admin.restaurant;

import com.michelin.service.restaurant.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/restaurants")
@RequiredArgsConstructor
public class AdminRestaurantController {
    private final RestaurantService restaurantService;

    @PostMapping("/update-ratings")
    public ResponseEntity<String> updateAllAvgRatings(){
        int updatedCount = restaurantService.updateAllAvgRating();
        return ResponseEntity.ok(" 총 " + updatedCount + "개의 레스토랑 avgRating 갱신 완료");
    }
}
