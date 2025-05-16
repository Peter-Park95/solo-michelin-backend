package com.michelin.service.restaurant;

import com.michelin.dto.restaurant.RestaurantRequest;
import com.michelin.dto.restaurant.RestaurantResponse;
import com.michelin.entity.restaurant.Restaurant;
import com.michelin.repository.restaurant.RestaurantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    @Transactional
    public RestaurantResponse createRestaurant(RestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setCategory(request.getCategory());
        restaurant.setMapUrl(request.getMarUrl());
        restaurant.setImageUrl(request.getImageUrl());
        restaurant.setAvgRating(0.0f);
        restaurant.setCreated(LocalDateTime.now());
        restaurant.setDeleted(0);

        Restaurant saved = restaurantRepository.save(restaurant);
        return RestaurantResponse.from(saved);
    }

    @Override
    public Page<RestaurantResponse> getAllRestaurants(Pageable pageable) {
        return restaurantRepository.findByDeletedFalse(pageable)
                .map(RestaurantResponse::from);
    }

    @Override
    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant r = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("음식점 정보를 찾을 수 없습니다."));
        return RestaurantResponse.from(r);
    }

    @Override
    @Transactional
    public RestaurantResponse updateRestaurant(Long id, RestaurantRequest request) {
        Restaurant r = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("음식점 정보를 찾을 수 없습니다."));

        r.setName(request.getName());
        r.setAddress(request.getAddress());
        r.setCategory(request.getCategory());
        r.setMapUrl(request.getMarUrl());
        r.setImageUrl(request.getImageUrl());

        return RestaurantResponse.from(restaurantRepository.save(r));
    }

    @Override
    @Transactional
    public void deleteRestaurant(Long id) {
        Restaurant r = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("음식점 정보를 찾을 수 없습니다."));

        r.setDeleted(1);
        restaurantRepository.save(r);
    }

    public List<RestaurantResponse> searchByName(String query) {
        List<Restaurant> results = restaurantRepository
                .findByNameContainingIgnoreCaseAndDeleted(query, 0);
        return results.stream()
                .map(RestaurantResponse::from)
                .collect(Collectors.toList());
    }

}
