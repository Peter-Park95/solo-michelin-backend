package com.michelin.controller;

import com.michelin.service.place.PlaceSearchService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/kakao-search")
public class PlaceSearchController {

	private final PlaceSearchService placeSearchService;

    @GetMapping
    public ResponseEntity<String> searchPlaces(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page // page 파라미터 추가
    ) {
        String result = placeSearchService.searchPlaces(query, page); // page 전달
        if (result == null) {
            return ResponseEntity.status(500).body("카카오 API 호출 실패");
        }
        return ResponseEntity.ok(result);
    }
}
