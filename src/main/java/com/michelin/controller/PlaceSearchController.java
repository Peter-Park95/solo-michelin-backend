package com.michelin.controller;

import com.michelin.service.PlaceSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kakao-search")
public class PlaceSearchController {

    @Autowired
    private PlaceSearchService placeSearchService;

    @GetMapping
    public String searchPlaces(@RequestParam String query){
        return placeSearchService.searchPlaces(query);
    }
}
