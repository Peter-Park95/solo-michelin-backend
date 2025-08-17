package com.michelin.controller;

import com.michelin.service.place.PlaceSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/kakao-search")
public class PlaceSearchController {

    private final PlaceSearchService placeSearchService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> searchPlaces(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Double x,
            @RequestParam(required = false) Double y,
            @RequestParam(defaultValue = "false") boolean nationwide
    ) {
        Long userId = currentUserIdOrNull();
        Map<String, Object> result = placeSearchService.searchPlaces(userId, query, page, x, y, nationwide);
        if (result == null) {
            return ResponseEntity.status(500).body(Map.of("error", "카카오 API 호출 실패"));
        }
        return ResponseEntity.ok(result);
    }

    private Long currentUserIdOrNull() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) return null;
            // TODO: 프로젝트의 인증 객체에 맞게 꺼내세요.
            // 예: return ((CustomUserDetails) auth.getPrincipal()).getId();
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}