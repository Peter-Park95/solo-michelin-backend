package com.michelin.controller.user;


import com.michelin.dto.user.KakaoLoginResponse;
import com.michelin.service.user.KakaoAuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @GetMapping("/api/auth/kakao/callback")
    public ResponseEntity<KakaoLoginResponse> kakaoCallback(@RequestParam String code) {
        String jwt = kakaoAuthService.kakaoLogin(code);
        return ResponseEntity.ok(new KakaoLoginResponse(jwt));
    }
}