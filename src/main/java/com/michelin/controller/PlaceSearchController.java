package com.michelin.controller;

import com.michelin.dto.PlaceDto;
import com.michelin.service.place.PlaceSearchService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/kakao-search")
public class PlaceSearchController {

	private final PlaceSearchService placeSearchService;

    @GetMapping
    public ResponseEntity<List<PlaceDto>> searchPlaces(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam double x,
            @RequestParam double y,
            @RequestParam Long userId
    ) {
        List<PlaceDto> result = placeSearchService.searchPlaces(query, page, x, y, userId); // page 전달
        return ResponseEntity.ok(result == null ? List.of() : result);
    }
}

