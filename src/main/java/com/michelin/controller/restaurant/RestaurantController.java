package com.michelin.controller.restaurant;

import com.michelin.dto.restaurant.RestaurantRequest;
import com.michelin.dto.restaurant.RestaurantResponse;
import com.michelin.service.restaurant.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public RestaurantResponse createRestaurant(@RequestBody @Valid RestaurantRequest request) {
        return restaurantService.createRestaurant(request);
    }

    @GetMapping
    public Page<RestaurantResponse> getAllRestaurants(Pageable pageable) {
        return restaurantService.getAllRestaurants(pageable);
    }

    @GetMapping("/{id}")
    public RestaurantResponse getRestaurantById(@PathVariable Long id) {
        return restaurantService.getRestaurantById(id);
    }

    @PutMapping("/{id}")
    public RestaurantResponse updateRestaurant(@PathVariable Long id, @RequestBody @Valid RestaurantRequest request) {
        return restaurantService.updateRestaurant(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
    }




    @GetMapping(params = "query")
    public List<RestaurantResponse> searchRestaurants(@RequestParam String query) {
        return restaurantService.searchByName(query);
    }


}
