package com.michelin.repository.restaurant;
import com.michelin.entity.restaurant.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
	
    // 기본 CRUD 제공 (findAll, findById, save, deleteById 등)
    List<Restaurant> findByNameContainingIgnoreCaseAndDeleted(String name, int deleted);
    
    Page<Restaurant> findByDeletedFalse(Pageable pageable);
    
    Optional<Restaurant> findByKakaoPlaceId(String kakaoPlaceId);
    
}