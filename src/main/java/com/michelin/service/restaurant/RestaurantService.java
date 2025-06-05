package com.michelin.service.restaurant;

import com.michelin.dto.restaurant.RestaurantRequest;
import com.michelin.dto.restaurant.RestaurantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RestaurantService {

    RestaurantResponse createRestaurant(RestaurantRequest request);
    Page<RestaurantResponse> getAllRestaurants(Pageable pageable);
    RestaurantResponse getRestaurantById(Long id);
    RestaurantResponse updateRestaurant(Long id, RestaurantRequest request);
    void deleteRestaurant(Long id);


    List<RestaurantResponse> searchByName(String query);
    int updateAllAvgRating();
}
